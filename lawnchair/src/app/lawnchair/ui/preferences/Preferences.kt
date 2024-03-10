/*
 * Copyright 2024, Lawnchair
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.lawnchair.ui.preferences

import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import app.lawnchair.backup.ui.createBackupGraph
import app.lawnchair.backup.ui.restoreBackupGraph
import app.lawnchair.ui.preferences.about.aboutGraph
import app.lawnchair.ui.preferences.components.colorpreference.colorSelectionGraph
import app.lawnchair.ui.preferences.destinations.PreferencesDashboard
import app.lawnchair.ui.preferences.destinations.appDrawerGraph
import app.lawnchair.ui.preferences.destinations.debugMenuGraph
import app.lawnchair.ui.preferences.destinations.dockGraph
import app.lawnchair.ui.preferences.destinations.experimentalFeaturesGraph
import app.lawnchair.ui.preferences.destinations.folderGraph
import app.lawnchair.ui.preferences.destinations.fontSelectionGraph
import app.lawnchair.ui.preferences.destinations.generalGraph
import app.lawnchair.ui.preferences.destinations.gesturesGraph
import app.lawnchair.ui.preferences.destinations.homeScreenGraph
import app.lawnchair.ui.preferences.destinations.iconPickerGraph
import app.lawnchair.ui.preferences.destinations.pickAppForGestureGraph
import app.lawnchair.ui.preferences.destinations.quickstepGraph
import app.lawnchair.ui.preferences.destinations.selectIconGraph
import app.lawnchair.ui.preferences.destinations.smartspaceGraph
import app.lawnchair.ui.preferences.destinations.smartspaceWidgetGraph
import app.lawnchair.ui.util.ProvideBottomSheetHandler
import app.lawnchair.util.ProvideLifecycleState
import com.google.accompanist.adaptive.HorizontalTwoPaneStrategy
import com.google.accompanist.adaptive.TwoPane
import soup.compose.material.motion.animation.materialSharedAxisXIn
import soup.compose.material.motion.animation.materialSharedAxisXOut
import soup.compose.material.motion.animation.rememberSlideDistance

object Routes {
    const val GENERAL = "general"
    const val ABOUT = "about"
    const val HOME_SCREEN = "homeScreen"
    const val DOCK = "dock"
    const val APP_DRAWER = "appDrawer"
    const val FOLDERS = "folders"
    const val QUICKSTEP = "quickstep"
    const val FONT_SELECTION = "fontSelection"
    const val COLOR_SELECTION = "colorSelection"
    const val DEBUG_MENU = "debugMenu"
    const val SELECT_ICON = "selectIcon"
    const val ICON_PICKER = "iconPicker"
    const val EXPERIMENTAL_FEATURES = "experimentalFeatures"
    const val SMARTSPACE = "smartspace"
    const val SMARTSPACE_WIDGET = "smartspaceWidget"
    const val CREATE_BACKUP = "createBackup"
    const val RESTORE_BACKUP = "restoreBackup"
    const val PICK_APP_FOR_GESTURE = "pickAppForGesture"
    const val GESTURES = "gestures"
}

val LocalNavController = staticCompositionLocalOf<NavController> {
    error("CompositionLocal LocalNavController not present")
}

val LocalPreferenceInteractor = staticCompositionLocalOf<PreferenceInteractor> {
    error("CompositionLocal LocalPreferenceInteractor not present")
}

@Composable
fun Preferences(
    windowSizeClass: WindowSizeClass,
    interactor: PreferenceInteractor = viewModel<PreferenceViewModel>(),
) {
    val navController = rememberNavController()
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val slideDistance = rememberSlideDistance()
    val isExpandedScreen = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded

    Providers {
        Surface {
            CompositionLocalProvider(
                LocalNavController provides navController,
                LocalPreferenceInteractor provides interactor,
            ) {
                PreferencesTwoPaneNav(
                    isExpandedScreen = isExpandedScreen,
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "/",
                        enterTransition = { materialSharedAxisXIn(!isRtl, slideDistance) },
                        exitTransition = { materialSharedAxisXOut(!isRtl, slideDistance) },
                        popEnterTransition = { materialSharedAxisXIn(isRtl, slideDistance) },
                        popExitTransition = { materialSharedAxisXOut(isRtl, slideDistance) },
                    ) {
                        preferenceGraph(
                            route = "/",
                            root = {
                                if (isExpandedScreen) {
                                    // do nothing
                                } else {
                                    PreferencesDashboard()
                                }
                            },
                        )
                        generalGraph(route = Routes.GENERAL)
                        homeScreenGraph(route = Routes.HOME_SCREEN)
                        dockGraph(route = Routes.DOCK)
                        appDrawerGraph(route = Routes.APP_DRAWER)
                        folderGraph(route = Routes.FOLDERS)
                        quickstepGraph(route = Routes.QUICKSTEP)
                        aboutGraph(route = Routes.ABOUT)
                        fontSelectionGraph(route = Routes.FONT_SELECTION)
                        colorSelectionGraph(route = Routes.COLOR_SELECTION)
                        debugMenuGraph(route = Routes.DEBUG_MENU)
                        selectIconGraph(route = Routes.SELECT_ICON)
                        iconPickerGraph(route = Routes.ICON_PICKER)
                        experimentalFeaturesGraph(route = Routes.EXPERIMENTAL_FEATURES)
                        smartspaceGraph(route = Routes.SMARTSPACE)
                        smartspaceWidgetGraph(route = Routes.SMARTSPACE_WIDGET)
                        createBackupGraph(route = Routes.CREATE_BACKUP)
                        restoreBackupGraph(route = Routes.RESTORE_BACKUP)
                        pickAppForGestureGraph(route = Routes.PICK_APP_FOR_GESTURE)
                        gesturesGraph(route = Routes.GESTURES)
                    }
                }
            }
        }
    }
}

@Composable
fun PreferencesTwoPaneNav(
    isExpandedScreen: Boolean = false,
    content: @Composable () -> Unit,
) {
    if (isExpandedScreen) {
        TwoPane(
            first = { PreferencesDashboard() },
            second = { content() },
            strategy = HorizontalTwoPaneStrategy(0.3f),
            displayFeatures = listOf(),
        )
    } else {
        content()
    }
}

@Composable
private fun Providers(
    content: @Composable () -> Unit,
) {
    ProvideLifecycleState {
        ProvideBottomSheetHandler {
            content()
        }
    }
}
