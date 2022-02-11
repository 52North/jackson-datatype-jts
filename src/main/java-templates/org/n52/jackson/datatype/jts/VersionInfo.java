/*
 * Copyright 2019-2022 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.jackson.datatype.jts;

import com.fasterxml.jackson.core.Version;

interface VersionInfo {
    String GROUP_ID = "${project.groupId}";
    String ARTIFACT_ID = "${project.artifactId}";
    String VERSION = "${project.version}";

    static Version getVersion() {
        String[] s = VersionInfo.VERSION.split("-");
        String snapshotInfo = null;
        int major = 0;
        int minor = 0;
        int patch = 0;
        if (s.length > 1) {
            snapshotInfo = s[1];
        }
        String[] v = s[0].split("\\.");

        if (v.length >= 3) {
            try {
                patch = Integer.parseInt(v[2], 10);
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (v.length >= 2) {
            try {
                minor = Integer.parseInt(v[1], 10);
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (v.length >= 1) {
            try {
                major = Integer.parseInt(v[0], 10);
            } catch (IllegalArgumentException ignored) {
            }
        }

        return new Version(major, minor, patch, snapshotInfo, GROUP_ID, ARTIFACT_ID);
    }
}