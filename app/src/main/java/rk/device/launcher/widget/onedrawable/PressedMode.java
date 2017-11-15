/*
 *     Copyright 2017 GuDong
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package rk.device.launcher.widget.onedrawable;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Press the state of the implementation
 */
public class PressedMode {
    @IntDef({ALPHA, DARK, WHITE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {}

    /**
     * 按下改变透明度
     */
    public static final int ALPHA = 0;
    /**
     * 按下增加黑色遮罩
     */
    public static final int DARK = 1;

	/**
	 * 按下增加白色遮罩
	 */
	public static final int WHITE = 2;

}
