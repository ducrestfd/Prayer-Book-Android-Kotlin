package com.myhobby.prayerbook

import android.app.Dialog
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.slider.Slider
import com.google.android.material.switchmaterial.SwitchMaterial
import java.text.DecimalFormat
import com.myhobby.prayerbook.R

class SpeechSettingsDialogFragment : DialogFragment() {

    private var tts: TextToSpeech? = null
    private var voices: List<Voice> = emptyList()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_speech_settings, null)

        val prefs = Prefs.get()
        val formatter = DecimalFormat("0.0x")

        // Rate Slider
        val rateSlider = view.findViewById<Slider>(R.id.rate_slider)
        val rateValueText = view.findViewById<TextView>(R.id.rate_value_text)
        rateSlider.value = prefs.getSpeechRate()
        rateValueText.text = formatter.format(rateSlider.value)
        rateSlider.addOnChangeListener { _, value, _ ->
            rateValueText.text = formatter.format(value)
        }

        // Pitch Slider
        val pitchSlider = view.findViewById<Slider>(R.id.pitch_slider)
        val pitchValueText = view.findViewById<TextView>(R.id.pitch_value_text)
        pitchSlider.value = prefs.getSpeechPitch()
        pitchValueText.text = formatter.format(pitchSlider.value)
        pitchSlider.addOnChangeListener { _, value, _ ->
            pitchValueText.text = formatter.format(value)
        }

        // Voice Selection
        val voiceSpinner = view.findViewById<Spinner>(R.id.voice_spinner)
        val langCode = prefs.getLanguage()

        tts = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Filter voices by the current language
                val availableVoices = tts?.voices?.filter {
                    it.locale.language == langCode
                } ?: emptyList()

                voices = availableVoices.sortedBy { it.name }

                requireActivity().runOnUiThread {
                    if (voices.isEmpty()) {
                        val noVoicesAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listOf("No voices available"))
                        voiceSpinner.adapter = noVoicesAdapter
                        voiceSpinner.isEnabled = false
                    } else {
                        val voiceNames = voices.map { it.name }
                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, voiceNames)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        voiceSpinner.adapter = adapter

                        val savedVoiceName = prefs.getSpeechVoice(langCode)
                        val selectedIndex = voices.indexOfFirst { it.name == savedVoiceName }
                        if (selectedIndex >= 0) {
                            voiceSpinner.setSelection(selectedIndex)
                        }
                    }
                }
            }
        }

        // Speak on open switch
        val speakOnOpenSwitch = view.findViewById<SwitchMaterial>(R.id.speak_on_open_switch)
        speakOnOpenSwitch.isChecked = prefs.speakPrayerOnOpen


        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.speech_settings)
            .setView(view)
            .setPositiveButton(R.string.ok) { _, _ ->
                // Save the final values when the user clicks OK
                prefs.setSpeechRate(rateSlider.value)
                prefs.setSpeechPitch(pitchSlider.value)
                prefs.speakPrayerOnOpen = speakOnOpenSwitch.isChecked

                val selectedIndex = voiceSpinner.selectedItemPosition
                if (voiceSpinner.isEnabled && selectedIndex >= 0 && selectedIndex < voices.size) {
                    prefs.setSpeechVoice(langCode, voices[selectedIndex].name)
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
    }

    override fun onDestroy() {
        super.onDestroy()
        tts?.shutdown()
    }

    companion object {
        const val TAG = "SpeechSettingsDialog"
    }
}
