package com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.search.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import com.vaxcare.unifiedhub.core.ui.component.button.PrimaryButton
import com.vaxcare.unifiedhub.feature.transactions.R

@Composable
fun VaxCareTheme.ConfirmLotNumberDialog(
    lotNumber: String,
    onConfirmClick: (String) -> Unit,
    onCancelClick: () -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = true
        ),
    ) {
        Column(
            modifier = Modifier
                .size(
                    width = measurement.size.dialogHub,
                    height = 268.dp
                ).background(
                    color = color.container.primaryContainer,
                    shape = RoundedCornerShape(size = measurement.radius.cardLarge)
                ).padding(measurement.spacing.medium)
        ) {
            Text(
                text = stringResource(
                    R.string.lot_search_confirm_add_lot_number_title_fmt,
                    lotNumber
                ),
                style = type.bodyTypeStyle.body3Bold
            )
            Spacer(Modifier.height(measurement.spacing.small))

            val html = stringResource(
                R.string.lot_search_confirm_add_lot_number_body_fmt,
                "<b>${stringResource(R.string.lot_search_confirm_add_lot_number_body_vaccine)}</b>",
                "<br/><br/>${stringResource(R.string.lot_search_confirm_add_lot_number_body2)}"
            )

            Text(
                text = AnnotatedString.fromHtml(html),
                style = type.bodyTypeStyle.body3
            )

            Spacer(Modifier.height(measurement.spacing.large))
            Row(
                modifier = Modifier
                    .width(640.dp)
                    .height(76.dp)
                    .padding(top = measurement.spacing.xSmall),
                verticalAlignment = androidx.compose.ui.Alignment.Bottom, // TODO fix bottom padding
                horizontalArrangement = Arrangement.End,
            ) {
                Button(
                    modifier = Modifier
                        .border(
                            2.dp,
                            color.outline.fourHundred,
                            RoundedCornerShape(size = measurement.radius.button)
                        ).widthIn(min = 120.dp),
                    colors = ButtonColors(
                        containerColor = color.container.primaryContainer,
                        contentColor = color.onContainer.onContainerPrimary,
                        disabledContainerColor = color.onContainer.disabled,
                        disabledContentColor = color.onContainer.primaryInverse
                    ),
                    onClick = {
                        onCancelClick()
                    },
                ) {
                    Text(
                        text = stringResource(R.string.lot_search_confirm_add_lot_number_cancel),
                        style = type.bodyTypeStyle.body5Bold
                    )
                }
                Spacer(Modifier.width(measurement.spacing.xSmall))
                PrimaryButton(
                    modifier = Modifier.widthIn(min = 120.dp),
                    onClick = { onConfirmClick(lotNumber) },
                    text = stringResource(R.string.lot_search_confirm_add_lot_number_confirm)
                )
            }
        }
    }
}

@Preview(
    device = "spec:width=1280dp,height=800dp,dpi=320,orientation=landscape",
    showBackground = true
)
@Composable
private fun ConfirmLotNumberDialogPreview() {
    VaxCareTheme {
        VaxCareTheme.ConfirmLotNumberDialog(
            lotNumber = "TEST1234",
            onConfirmClick = {},
            onCancelClick = {}
        )
    }
}
