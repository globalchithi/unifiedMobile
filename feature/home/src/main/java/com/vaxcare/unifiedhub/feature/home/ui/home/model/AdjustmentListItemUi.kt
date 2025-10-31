package com.vaxcare.unifiedhub.feature.home.ui.home.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.vaxcare.unifiedhub.feature.home.R
import com.vaxcare.unifiedhub.core.designsystem.R as designR

sealed class AdjustmentListItemUi(
    @StringRes val titleResId: Int,
    @DrawableRes val iconResId: Int
) {
    data object AddPublic : AdjustmentListItemUi(
        titleResId = R.string.add_public,
        iconResId = designR.drawable.ic_plus
    )

    data object Returns : AdjustmentListItemUi(
        titleResId = R.string.returns,
        iconResId = designR.drawable.ic_arrow_return
    )

    data object LogWaste : AdjustmentListItemUi(
        titleResId = R.string.log_waste,
        iconResId = designR.drawable.ic_delete
    )

    data object Transfer : AdjustmentListItemUi(
        titleResId = R.string.transfer,
        iconResId = designR.drawable.ic_arrow_transfer
    )

    data object Buyback : AdjustmentListItemUi(
        titleResId = R.string.buyback,
        iconResId = designR.drawable.ic_buyback
    )
}
