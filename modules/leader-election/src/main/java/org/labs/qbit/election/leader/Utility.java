package org.labs.qbit.election.leader;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Copyright (c) 2013, QBit-Labs Inc. (http://qbit-labs.org) All Rights Reserved.
 *
 * QBit-Labs Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
public class Utility {

    private static int idIncrementationIndex = 0;
    private static final String DATA_FORMAT = "yyMMddHHmmSSS";
    private static final String DECIMAL_FORMAT_CORRELATION_ID = "0000";

    public static String getUniqueId() {
        int localIndex;
        synchronized (Utility.class) {
            if (idIncrementationIndex >= 9999) {
                idIncrementationIndex = 0;
            }
            localIndex = ++idIncrementationIndex;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATA_FORMAT);
        Date date = new Date();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(dateFormat.format(date));
        DecimalFormat decimalFormat = new DecimalFormat(DECIMAL_FORMAT_CORRELATION_ID);
        stringBuilder.append(decimalFormat.format(localIndex));
        return stringBuilder.toString();
    }
}
