package com.vaxcare.unifiedhub.feature.home.ui.home.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.color
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.ui.compose.LocalStock
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.home.R
import com.vaxcare.unifiedhub.feature.home.ui.home.model.Notification
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

@Composable
internal fun NotificationsCard(
    notifications: Set<Notification>,
    modifier: Modifier = Modifier,
    onNotificationClick: (Notification) -> Unit,
) {
    val cardColor by animateColorAsState(
        targetValue = if (notifications.isNotEmpty()) {
            color.container.primaryContainer
        } else {
            color.container.neutralPress
        }
    )

    Surface(
        shape = RoundedCornerShape(measurement.radius.cardLarge),
        color = cardColor,
        modifier = modifier.width(504.dp).heightIn(max = 434.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(vertical = measurement.spacing.small)
                .animateContentSize()
                .verticalScroll(rememberScrollState())
            // TODO: this will need a fading edge once there are enough notifications to
            //  cause scrolling
        ) {
            if (notifications.isNotEmpty()) {
                NotificationList(
                    notifications = notifications,
                    onNotificationClick = onNotificationClick
                )
            } else {
                EmptyNotifications()
            }
        }
    }
}

@Composable
internal fun NotificationList(notifications: Set<Notification>, onNotificationClick: (Notification) -> Unit,) {
    notifications
        .sortedBy { it.position }
        .forEachIndexed { i, notification ->
            when (notification) {
                is Notification.OverdueCount -> {
                    NotificationItem(
                        title = stringResource(R.string.notification_overdue_count_title),
                        description = stringResource(
                            R.string.notification_overdue_count_description,
                            notification.daysOverdue
                        ),
                        onClick = {
                            onNotificationClick(notification)
                        }
                    )
                }

                is Notification.ExpiredDoses -> {
                    NotificationItem(
                        title = stringResource(R.string.notification_expired_title),
                        description = stringResource(
                            R.string.notification_expired_description,
                            notification.count
                        ),
                        onClick = {
                            onNotificationClick(notification)
                        }
                    )
                }

                is Notification.AppUpdate -> {
                    NotificationItem(
                        title = stringResource(R.string.notification_app_update_title),
                        description = stringResource(R.string.notification_app_update_description),
                        onClick = {
                            onNotificationClick(notification)
                        }
                    )
                }
            }

            if (i != notifications.size - 1) {
                HorizontalDivider(
                    modifier = Modifier.padding(
                        vertical = measurement.spacing.xSmall,
                        horizontal = measurement.spacing.xSmall
                    )
                )
            }
        }
}

@Composable
internal fun EmptyNotifications(modifier: Modifier = Modifier) {
    val logoRes = when (LocalStock.current) {
        StockUi.PRIVATE -> DesignSystemR.drawable.ic_logo_purple
        StockUi.VFC -> DesignSystemR.drawable.ic_logo_green
        StockUi.STATE -> DesignSystemR.drawable.ic_logo_pink
        StockUi.THREE_SEVENTEEN -> DesignSystemR.drawable.ic_logo_blue
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.background(color.container.neutralPress)
    ) {
        Image(
            painter = painterResource(logoRes),
            contentDescription = null,
            modifier = Modifier
                .padding(
                    top = 69.dp,
                    bottom = 72.dp
                ).size(96.dp)
        )

        Text(
            text = stringResource(R.string.notifications_empty_message),
            style = type.bodyTypeStyle.body3,
            color = color.onContainer.info,
            modifier = Modifier.padding(bottom = 37.dp)
        )
    }
}

@Composable
internal fun NotificationItem(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                horizontal = measurement.spacing.medium,
                vertical = measurement.spacing.xSmall
            ).then(modifier)
    ) {
        Text(
            text = title,
            style = type.bodyTypeStyle.body4Bold
        )

        Text(
            text = description,
            style = type.bodyTypeStyle.body4
        )
    }
}
