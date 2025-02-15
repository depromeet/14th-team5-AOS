package com.no5ing.bbibbi.presentation.feature.view.landing.day_of_birth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.component.BBiBBiSurface
import com.no5ing.bbibbi.presentation.component.DigitizedNumberInput
import com.no5ing.bbibbi.presentation.component.DisposableTopBar
import com.no5ing.bbibbi.presentation.component.button.CTAButton
import com.no5ing.bbibbi.presentation.feature.state.register.day_of_birth.RegisterDayOfBirthPageState
import com.no5ing.bbibbi.presentation.feature.state.register.day_of_birth.rememberRegisterDayOfBirthPageState
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import java.time.YearMonth

@Composable
fun RegisterDayOfBirthPage(
    onDispose: () -> Unit,
    nickName: String,
    onNextPage: (String) -> Unit,
    state: RegisterDayOfBirthPageState = rememberRegisterDayOfBirthPageState()
) {
    val yearFocus = remember { FocusRequester() }
    val monthFocus = remember { FocusRequester() }
    val dayFocus = remember { FocusRequester() }
    val localFocusManager = LocalFocusManager.current
    LaunchedEffect(Unit) {
        yearFocus.requestFocus()
    }
    BBiBBiSurface(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
            .padding(horizontal = 10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DisposableTopBar(onDispose = onDispose, title = "")
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(
                        id = R.string.register_day_of_birth_description,
                        nickName
                    ),
                    color = MaterialTheme.bbibbiScheme.textSecondary,
                    style = MaterialTheme.bbibbiTypo.headTwoBold,
                    textAlign = TextAlign.Center,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DigitizedNumberInput(
                        baseDigit = 4,
                        digitName = stringResource(id = R.string.register_day_of_birth_year),
                        value = state.yearTextState.value,
                        focusRequester = yearFocus,
                        onValueChange = {
                            val number = it.toIntOrNull()
                            if (number == null) {
                                state.yearTextState.value = ""
                                return@DigitizedNumberInput
                            }
                            if (number < 0 || number > 9999) return@DigitizedNumberInput
                            state.isInvalidYearState.value =
                                number > YearMonth.now().year || number < 1900 //TODO
                            if (it.length == 4 && (state.yearTextState.value.toIntOrNull()
                                    ?: 0) / 100 > 0
                            ) monthFocus.requestFocus()
                            state.yearTextState.value = it
                        },
                        isInvalidInput = state.isInvalidInput(),
                        onDone = { monthFocus.requestFocus() }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    DigitizedNumberInput(
                        baseDigit = 2,
                        digitName = stringResource(id = R.string.register_day_of_birth_month),
                        value = state.monthTextState.value,
                        focusRequester = monthFocus,
                        onValueChange = {
                            val number = it.toIntOrNull()
                            if (number == null) {
                                state.monthTextState.value = ""
                                return@DigitizedNumberInput
                            }
                            if (number < 0 || number > 99) return@DigitizedNumberInput
                            state.isInvalidMonthState.value = number > 12
                            if (it.length == 2 && (state.monthTextState.value.toIntOrNull()
                                    ?: 0) < 10
                            ) dayFocus.requestFocus()
                            state.monthTextState.value = it
                        },
                        isInvalidInput = state.isInvalidInput(),
                        onDone = { dayFocus.requestFocus() }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    DigitizedNumberInput(
                        baseDigit = 2,
                        digitName = stringResource(id = R.string.register_day_of_birth_day),
                        value = state.dayTextState.value,
                        focusRequester = dayFocus,
                        onValueChange = {
                            val number = it.toIntOrNull()
                            if (number == null) {
                                state.dayTextState.value = ""
                                return@DigitizedNumberInput
                            }
                            if (number < 0 || number > 99) return@DigitizedNumberInput
                            state.isInvalidDayState.value = number > 31
                            state.dayTextState.value = it
                        },
                        isInvalidInput = state.isInvalidInput(),
                        onDone = { localFocusManager.clearFocus() }
                    )
                }
                if (state.isInvalidInput()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.warning_circle_icon),
                            contentDescription = null,
                            modifier = Modifier
                                .size(20.dp),
                            tint = MaterialTheme.bbibbiScheme.warningRed,
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(id = R.string.register_dob_correct_date),
                            color = MaterialTheme.bbibbiScheme.warningRed,
                            style = MaterialTheme.bbibbiTypo.bodyOneRegular,
                        )
                    }
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.register_dob_description),
                    color = MaterialTheme.bbibbiScheme.icon,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.bbibbiTypo.bodyOneRegular,
                )
                CTAButton(
                    text = stringResource(id = R.string.register_continue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentPadding = PaddingValues(vertical = 18.dp),
                    onClick = {
                        val yearStr = String.format("%04d", state.yearTextState.value.toInt())
                        val monthStr = String.format("%02d", state.monthTextState.value.toInt())
                        val dayStr = String.format("%02d", state.dayTextState.value.toInt())
                        onNextPage(
                            "${yearStr}-${monthStr}-${dayStr}"
                        )
                    },
                    isActive = !state.isInvalidInput() && state.dayTextState.value.isNotEmpty()
                            && state.monthTextState.value.isNotEmpty() && state.yearTextState.value.isNotEmpty(),
                )
            }

        }
    }
}