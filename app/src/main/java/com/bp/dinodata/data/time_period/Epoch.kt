package com.bp.dinodata.data.time_period

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.bp.dinodata.R
import com.bp.dinodata.presentation.icons.chronology.ITimeInterval
import com.bp.dinodata.presentation.icons.chronology.IDisplayableTimePeriod
import com.bp.dinodata.presentation.icons.chronology.TimeInterval
import com.bp.dinodata.theme.Cretaceous300
import com.bp.dinodata.theme.Cretaceous600
import com.bp.dinodata.theme.Jurassic300
import com.bp.dinodata.theme.Jurassic700
import com.bp.dinodata.theme.Triassic200
import com.bp.dinodata.theme.Triassic800
import com.bp.dinodata.theme.cambrianDark
import com.bp.dinodata.theme.cambrianLight
import com.bp.dinodata.theme.carboniferousDark
import com.bp.dinodata.theme.carboniferousLight
import com.bp.dinodata.theme.devonianDark
import com.bp.dinodata.theme.devonianLight
import com.bp.dinodata.theme.neogeneDark
import com.bp.dinodata.theme.neogeneLight
import com.bp.dinodata.theme.ordovicianDark
import com.bp.dinodata.theme.ordovicianLight
import com.bp.dinodata.theme.paleogeneDark
import com.bp.dinodata.theme.paleogeneLight
import com.bp.dinodata.theme.permianDark
import com.bp.dinodata.theme.permianLight
import com.bp.dinodata.theme.quaternaryDark
import com.bp.dinodata.theme.quaternaryLight
import com.bp.dinodata.theme.silurianDark
import com.bp.dinodata.theme.silurianLight

interface IAtomicDisplayableTimePeriod: IDisplayableTimePeriod
interface ISubTimePeriod: IDisplayableTimePeriod

interface IEpoch: IAtomicDisplayableTimePeriod

abstract class Epoch(
    private val nameResId: Int,
    private val timePeriodRange: ITimeInterval,
    private val colorLight: Color,
    private val colorDark: Color = colorLight
): IEpoch {
    @Composable
    override fun getName(): String {
        return stringResource(nameResId)
    }
    override fun getStartTimeInMYA(): Float = timePeriodRange.getStartTimeInMYA()
    override fun getEndTimeInMYA(): Float = timePeriodRange.getEndTimeInMYA()
    override fun overlapsWith(other: ITimeInterval): Boolean =
        timePeriodRange.overlapsWith(other)

    override fun getBrushDarkToBright(): Brush = Brush.linearGradient(
        0.0f to colorDark,
        1.0f to colorLight
    )
    override fun getBrushBrightToDark(): Brush {
        return Brush.linearGradient(
            0.0f to colorLight,
            1.0f to colorDark
        )
    }
}




sealed class MesozoicEpoch(
    nameResId: Int,
    timePeriodRange: ITimeInterval,
    colorLight: Color,
    colorDark: Color = colorLight
): Epoch(nameResId, timePeriodRange, colorLight, colorDark) {
    data object Triassic: MesozoicEpoch(
        nameResId = R.string.time_period_triassic,
        timePeriodRange = TimeInterval(251.9f, 201.4f),
        colorLight = Triassic200,
        colorDark = Triassic800
    )
    data object Jurassic: MesozoicEpoch(
        timePeriodRange = TimeInterval(201.4f, 145f),
        nameResId = R.string.time_period_jurassic,
        colorLight = Jurassic300,
        colorDark = Jurassic700
    )
    data object Cretaceous: MesozoicEpoch(
        timePeriodRange = TimeInterval(145f, 65.5f),
        nameResId = R.string.time_period_cretaceous,
        colorLight = Cretaceous300,
        colorDark = Cretaceous600
    )
}



sealed class PaleozoicEpoch(nameResId: Int,
                            timePeriodRange: ITimeInterval,
                            colorLight: Color,
                            colorDark: Color = colorLight
): Epoch(nameResId, timePeriodRange, colorLight, colorDark) {
    data object Ordovician: PaleozoicEpoch(
        nameResId = R.string.time_period_ordovician,
        timePeriodRange = TimeInterval(485.4f, 443.8f),
        colorLight = ordovicianLight,
        colorDark = ordovicianDark
    )
    data object Cambrian: PaleozoicEpoch(
        nameResId = R.string.time_period_cambrian,
        timePeriodRange = TimeInterval(538.8f, 485.4f),
        colorLight = cambrianLight,
        colorDark = cambrianDark
    )
    data object Permian: PaleozoicEpoch(
        nameResId = R.string.time_period_permian,
        timePeriodRange = TimeInterval(298.9f, 251.9f),
        colorLight = permianLight,
        colorDark = permianDark
    )

    data object Carboniferous: PaleozoicEpoch(
        nameResId = R.string.time_period_carboniferous,
        timePeriodRange = TimeInterval(358.9f, 298.9f),
        colorLight = carboniferousLight,
        colorDark = carboniferousDark
    )

    data object Devonian: PaleozoicEpoch(
        nameResId = R.string.time_period_devonian,
        timePeriodRange = TimeInterval(419.2f, 358.9f),
        colorLight = devonianLight,
        colorDark = devonianDark
    )

    data object Silurian: PaleozoicEpoch(
        nameResId = R.string.time_period_silurian,
        timePeriodRange = TimeInterval(443.8f, 419.2f),
        colorLight = silurianLight,
        colorDark = silurianDark
    )
}

sealed class CenozoicEpoch(nameResId: Int,
                           timePeriodRange: ITimeInterval,
                           colorLight: Color,
                           colorDark: Color = colorLight
): Epoch(nameResId, timePeriodRange, colorLight, colorDark) {

    data object Paleogene: CenozoicEpoch(
        nameResId = R.string.time_period_paleogene,
        timePeriodRange = TimeInterval(66f, 23.03f),
        colorLight = paleogeneLight,
        colorDark = paleogeneDark
    )
    data object Neogene: CenozoicEpoch(
        nameResId = R.string.time_period_neogene,
        timePeriodRange = TimeInterval(23.03f, 2.58f),
        colorLight = neogeneLight,
        colorDark = neogeneDark
    )
    data object Quaternary: CenozoicEpoch(
        nameResId = R.string.time_period_quaternary,
        timePeriodRange = TimeInterval(2.58f, 0f),
        colorLight = quaternaryLight,
        colorDark = quaternaryDark
    )
}


enum class Subepoch {
    Early, Middle, Late
}

data class TimePeriod(
    val epoch: IEpoch,
    private val subepoch: Subepoch? = null
): IDisplayableTimePeriod {
    override fun getBrushDarkToBright(): Brush = epoch.getBrushDarkToBright()
    override fun getBrushBrightToDark(): Brush = epoch.getBrushBrightToDark()

    override fun getStartTimeInMYA(): Float = epoch.getStartTimeInMYA()
    override fun getEndTimeInMYA(): Float = epoch.getEndTimeInMYA()

    @Composable
    override fun getName(): String {
        val sb = StringBuilder()
        if (subepoch != null) {
            sb.append("$subepoch ")
        }
        return sb.append(epoch.getName()).toString()
    }
}

data class SubTimePeriod(
    val epoch: IAtomicDisplayableTimePeriod,
    val subepoch: Subepoch
): ISubTimePeriod {
    override fun getBrushDarkToBright(): Brush = epoch.getBrushDarkToBright()
    override fun getBrushBrightToDark(): Brush = epoch.getBrushBrightToDark()
    override fun getStartTimeInMYA(): Float = epoch.getStartTimeInMYA()
    override fun getEndTimeInMYA(): Float = epoch.getEndTimeInMYA()

    @Composable
    override fun getName(): String {
        return "$subepoch ${epoch.getName()}"
    }
}