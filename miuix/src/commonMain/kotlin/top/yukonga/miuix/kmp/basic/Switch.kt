package top.yukonga.miuix.kmp.basic

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitVerticalTouchSlopOrCancellation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlin.math.absoluteValue

/**
 * A [Switch] component with Miuix style.
 *
 * @param checked The checked state of the [Switch].
 * @param onCheckedChange The callback to be called when the state of the [Switch] changes.
 * @param modifier The modifier to be applied to the [Switch].
 * @param colors The [SwitchColors] of the [Switch].
 * @param enabled Whether the [Switch] is enabled.
 */
@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    colors: SwitchColors = SwitchDefaults.switchColors(),
    enabled: Boolean = true
) {
    val isChecked by rememberUpdatedState(checked)

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isDragged by interactionSource.collectIsDraggedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()

    val hapticFeedback = LocalHapticFeedback.current
    var hasVibrated by remember { mutableStateOf(true) }
    var hasVibratedOnce by remember { mutableStateOf(false) }

    val springSpec = remember {
        spring<Dp>(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        )
    }

    var dragOffset by remember { mutableStateOf(0f) }
    val thumbOffset by animateDpAsState(
        targetValue = if (isChecked) {
            if (!enabled) 26.dp else if (isPressed || isDragged || isHovered) 24.dp else 26.dp
        } else {
            if (!enabled) 4.dp else if (isPressed || isDragged || isHovered) 3.dp else 4.dp
        } + dragOffset.dp,
        animationSpec = springSpec
    )

    val thumbSize by animateDpAsState(
        targetValue = if (!enabled) 20.dp else if (isPressed || isDragged || isHovered) 23.dp else 20.dp,
        animationSpec = springSpec
    )

    val thumbColor by animateColorAsState(
        if (isChecked) colors.checkedThumbColor(enabled) else colors.uncheckedThumbColor(enabled)
    )

    val backgroundColor by animateColorAsState(
        if (isChecked) colors.checkedTrackColor(enabled) else colors.uncheckedTrackColor(enabled),
        animationSpec = tween(durationMillis = 200)
    )

    Box(
        modifier = modifier
            .wrapContentSize(Alignment.Center)
            .size(50.dp, 28.5.dp)
            .requiredSize(50.dp, 28.5.dp)
            .clip(RoundedCornerShape(100.dp))
            .drawBehind { drawRect(backgroundColor) }
            .hoverable(
                interactionSource = interactionSource,
                enabled = enabled
            )
            .pointerInput(Unit) {
                if (!enabled) return@pointerInput
                val touchSlop = 16f
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val initialOffset = down.position
                    var validHorizontalDrag = false
                    do {
                        val event = awaitPointerEvent()
                        val currentOffset = event.changes[0].position
                        val dx = (currentOffset.x - initialOffset.x).absoluteValue
                        val dy = (currentOffset.y - initialOffset.y).absoluteValue
                        if (dy > touchSlop) {
                            validHorizontalDrag = false
                            break
                        } else if (dx > touchSlop) {
                            validHorizontalDrag = true
                        }
                    } while (event.changes.all { it.pressed })

                    if (validHorizontalDrag && !isPressed && !isDragged) {
                        onCheckedChange?.invoke(!isChecked)
                        hapticFeedback.performHapticFeedback(
                            if (isChecked) HapticFeedbackType.ToggleOff
                            else HapticFeedbackType.ToggleOn
                        )
                    }
                }
            }
            .toggleable(
                value = isChecked,
                onValueChange = {
                    if (onCheckedChange == null) return@toggleable
                    onCheckedChange.invoke(it)
                    hapticFeedback.performHapticFeedback(
                        if (it) HapticFeedbackType.ToggleOn
                        else HapticFeedbackType.ToggleOff
                    )
                },
                enabled = enabled,
                role = Role.Switch,
                indication = null,
                interactionSource = null
            )
    ) {
        Box(
            modifier = Modifier
                .padding(start = thumbOffset)
                .align(Alignment.CenterStart)
                .size(thumbSize)
                .background(
                    color = thumbColor,
                    shape = RoundedCornerShape(100.dp)
                )
                .pointerInput(Unit) {
                    if (!enabled) return@pointerInput
                    awaitEachGesture {
                        val pressInteraction: PressInteraction.Press
                        val down = awaitFirstDown().also {
                            pressInteraction = PressInteraction.Press(it.position)
                            interactionSource.tryEmit(pressInteraction)
                        }
                        waitForUpOrCancellation().also {
                            interactionSource.tryEmit(PressInteraction.Cancel(pressInteraction))
                        }
                        awaitVerticalTouchSlopOrCancellation(down.id) { _, _ ->
                            interactionSource.tryEmit(PressInteraction.Cancel(pressInteraction))
                        }
                    }
                }
                .draggable(
                    state = rememberDraggableState { delta ->
                        if (onCheckedChange == null) return@rememberDraggableState
                        dragOffset = (dragOffset + delta / 2).let {
                            if (isChecked) it.coerceIn(-21f, 0f) else it.coerceIn(0f, 21f)
                        }
                        if (dragOffset in -11f..-10f || dragOffset in 10f..11f) {
                            hasVibratedOnce = false
                        } else if (dragOffset in -20f..-1f || dragOffset in 1f..20f) {
                            hasVibrated = false
                        } else if (!hasVibrated) {
                            if ((isChecked && dragOffset == -21f) || (!isChecked && dragOffset == 0f)) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOff)
                                hasVibrated = true
                                hasVibratedOnce = true
                            } else if ((isChecked && dragOffset == 0f) || (!isChecked && dragOffset == 21f)) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOn)
                                hasVibrated = true
                                hasVibratedOnce = true
                            }
                        }
                    },
                    orientation = Orientation.Horizontal,
                    enabled = enabled,
                    interactionSource = interactionSource,
                    onDragStopped = {
                        if (dragOffset.absoluteValue > 21f / 2) onCheckedChange?.invoke(!isChecked)
                        if (!hasVibratedOnce && dragOffset.absoluteValue >= 1f) {
                            if ((isChecked && dragOffset <= -11f) || (!isChecked && dragOffset <= 10f)) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOff)
                            } else if ((isChecked && dragOffset >= -10f) || (!isChecked && dragOffset >= 11f)) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOn)
                            }
                        }
                        hasVibrated = true
                        hasVibratedOnce = false
                        dragOffset = 0f
                    }
                )
        )
    }
}

object SwitchDefaults {

    /**
     * The default colors for the [Switch].
     */
    @Composable
    fun switchColors(
        checkedThumbColor: Color = MiuixTheme.colorScheme.onPrimary,
        uncheckedThumbColor: Color = MiuixTheme.colorScheme.onSecondary,
        disabledCheckedThumbColor: Color = MiuixTheme.colorScheme.disabledOnPrimary,
        disabledUncheckedThumbColor: Color = MiuixTheme.colorScheme.disabledOnSecondary,
        checkedTrackColor: Color = MiuixTheme.colorScheme.primary,
        uncheckedTrackColor: Color = MiuixTheme.colorScheme.secondary,
        disabledCheckedTrackColor: Color = MiuixTheme.colorScheme.disabledPrimary,
        disabledUncheckedTrackColor: Color = MiuixTheme.colorScheme.disabledSecondary
    ): SwitchColors {
        return SwitchColors(
            checkedThumbColor = checkedThumbColor,
            uncheckedThumbColor = uncheckedThumbColor,
            disabledCheckedThumbColor = disabledCheckedThumbColor,
            disabledUncheckedThumbColor = disabledUncheckedThumbColor,
            checkedTrackColor = checkedTrackColor,
            uncheckedTrackColor = uncheckedTrackColor,
            disabledCheckedTrackColor = disabledCheckedTrackColor,
            disabledUncheckedTrackColor = disabledUncheckedTrackColor
        )
    }
}

@Immutable
class SwitchColors(
    private val checkedThumbColor: Color,
    private val uncheckedThumbColor: Color,
    private val disabledCheckedThumbColor: Color,
    private val disabledUncheckedThumbColor: Color,
    private val checkedTrackColor: Color,
    private val uncheckedTrackColor: Color,
    private val disabledCheckedTrackColor: Color,
    private val disabledUncheckedTrackColor: Color
) {
    @Stable
    internal fun checkedThumbColor(enabled: Boolean): Color =
        if (enabled) checkedThumbColor else disabledCheckedThumbColor

    @Stable
    internal fun uncheckedThumbColor(enabled: Boolean): Color =
        if (enabled) uncheckedThumbColor else disabledUncheckedThumbColor

    @Stable
    internal fun checkedTrackColor(enabled: Boolean): Color =
        if (enabled) checkedTrackColor else disabledCheckedTrackColor

    @Stable
    internal fun uncheckedTrackColor(enabled: Boolean): Color =
        if (enabled) uncheckedTrackColor else disabledUncheckedTrackColor
}