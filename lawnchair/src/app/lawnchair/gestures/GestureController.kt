/*
 * Copyright 2021, Lawnchair
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

package app.lawnchair.gestures

import androidx.lifecycle.lifecycleScope
import app.lawnchair.LawnchairLauncher
import app.lawnchair.gestures.config.GestureHandlerConfig
import app.lawnchair.preferences2.PreferenceManager2
import com.patrykmichalik.preferencemanager.Preference
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GestureController(private val launcher: LawnchairLauncher) {
    private val prefs = PreferenceManager2.getInstance(launcher)
    private val scope = MainScope()

    private val doubleTapHandler = handler(prefs.doubleTapHandler)
    private val swipeUpHandler = handler(prefs.swipeUpGestureHandler)
    private val swipeDownHandler = handler(prefs.swipeDownGestureHandler)

    fun onDoubleTap() {
        triggerHandler(doubleTapHandler)
    }

    fun onSwipeUp() {
        triggerHandler(swipeUpHandler)
    }

    fun onSwipeDown() {
        triggerHandler(swipeDownHandler)
    }

    private fun triggerHandler(handlerFlow: Flow<GestureHandler>) {
        launcher.lifecycleScope.launch {
            handlerFlow.first().onTrigger(launcher)
        }
    }

    private fun handler(pref: Preference<GestureHandlerConfig, String>) = pref.get()
        .distinctUntilChanged()
        .map { it.createHandler(launcher) }
        .shareIn(
            scope,
            SharingStarted.Lazily,
            replay = 1
        )
}
