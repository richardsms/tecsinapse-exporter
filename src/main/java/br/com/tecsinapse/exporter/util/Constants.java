/*
 * TecSinapse Exporter
 *
 * License: GNU Lesser General Public License (LGPL), version 3 or later
 * See the LICENSE file in the root directory or <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package br.com.tecsinapse.exporter.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public interface Constants {
    short DECIMAL_PRECISION = 10;
    LocalDate LOCAL_DATE_BIGBANG = LocalDate.of(1899, 12, 31);
    Locale LOCALE_PT_BR = new Locale("pt", "BR");
    Locale LOCALE_ES_ES = new Locale("es", "ES");
    DateTimeFormatter DATE_TIME_FILE_NAME = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
    DateTimeFormatter LOCAL_TIME_ISO_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    DateTimeFormatter LOCAL_DATE_ISO_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter LOCAL_DATE_TIME_ISO_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
}
