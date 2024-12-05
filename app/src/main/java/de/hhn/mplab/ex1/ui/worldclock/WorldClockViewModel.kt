package de.hhn.mplab.ex1.ui.worldclock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

class WorldClockViewModel : ViewModel() {
    private val _state = MutableStateFlow(WorldClockState())
    val state: StateFlow<WorldClockState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            while (true) {
                updateSystemDateTime()
                updateSelectedDateTime()
                delay(1000L)
            }
        }
    }

    /**
     * Updates the system date and time in the app's state based on the system's default locale.
     *
     * This function retrieves the current system date and time formatted according to the system's default locale
     * and updates the app's state with these values. It does not use the user-selected locale or time zone,
     * ensuring the display reflects the device's system settings.
     */
    private fun updateSystemDateTime() {
        _state.value = _state.value.copy(
            currentSystemTime = getTime(_state.value.systemDefaultLocale),
            currentSystemDate = getDate(_state.value.systemDefaultLocale)
        )
    }

    /**
     * Updates the currently selected date and time in the app's state based on the selected locale and time zone.
     *
     * This function retrieves the current date and time formatted according to the user-selected locale and time zone,
     * then updates the app's state with these values.
     */
    private fun updateSelectedDateTime() {
        _state.value = _state.value.copy(
            currentSelectedTime = getTime(
                _state.value.selectedLocale,
                _state.value.selectedTimeZone
            ),
            currentSelectedDate = getDate(
                _state.value.selectedLocale,
                _state.value.selectedTimeZone
            )
        )
    }

    /**
     * Retrieves the current time as a formatted string according to the specified locale and time zone.
     *
     * @param locale The locale to be used for formatting the time.
     * @param timeZone The time zone to get the current time in. Defaults to the system's time zone.
     * @return A string representing the current time formatted in the medium style for the given locale and time zone.
     */
    private fun getTime(locale: Locale, timeZone: ZoneId = ZoneId.systemDefault()): String {
        val formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM).withLocale(locale)
            .withZone(timeZone)
        return ZonedDateTime.now().format(formatter).toString()
    }

    /**
     * Retrieves the current date as a formatted string according to the specified locale and time zone.
     *
     * @param locale The locale to be used for formatting the date.
     * @param timeZone The time zone to get the current date in. Defaults to the system's time zone.
     * @return A string representing the current date formatted in the medium style for the given locale and time zone.
     */
    private fun getDate(locale: Locale, timeZone: ZoneId = ZoneId.systemDefault()): String {
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
            .withZone(timeZone)
        return ZonedDateTime.now().format(formatter).toString()
    }

    /**
     * Updates the app's state with a new selected time zone.
     *
     * This function sets the selected time zone in the state and allows other functions
     * to retrieve the date and time in the newly specified time zone.
     *
     * @param zoneId The time zone to be set as the selected time zone in the app's state.
     */
    fun setTimezone(zoneId: ZoneId) {
        _state.value = _state.value.copy(
            selectedTimeZone = zoneId,
        )
    }

    /**
     * Updates the app's state with a new selected locale.
     *
     * This function sets the selected locale in the state, allowing other functions
     * to format the date and time according to the newly specified locale.
     *
     * @param locale The locale to be set as the selected locale in the app's state.
     */
    fun setLocale(locale: Locale) {
        _state.value = _state.value.copy(
            selectedLocale = locale,
        )
    }

    /**
     * Retrieves a set of all available time zone IDs supported by the system.
     *
     * This function provides a list of time zones that can be used in the app, allowing the user
     * to select from valid time zone IDs. Each time zone ID represents a geographical region or
     * a specific offset from UTC.
     *
     * @return A set of strings, each representing a unique time zone ID.
     */
    fun getAvailableTimezones(): Set<String> {
        return ZoneId.getAvailableZoneIds()
    }

    /**
     * Retrieves an array of all available locales supported by the system.
     *
     * This function provides a list of locales that can be used in the app for localization purposes,
     * allowing the user to select from supported languages and regional settings. Each locale represents
     * a specific language and regional formatting standard.
     *
     * @return An array of `Locale` objects, each representing a unique locale supported by the system.
     */
    fun getAvailableLocales(): Array<Locale> {
        return Locale.getAvailableLocales()
    }

}