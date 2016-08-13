/*
 * TecSinapse Exporter
 *
 * License: GNU Lesser General Public License (LGPL), version 3 or later
 * See the LICENSE file in the root directory or <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package br.com.tecsinapse.exporter.converter;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import com.google.common.base.Strings;

public class YearMonthTableCellConverter implements TableCellConverter<YearMonth> {

    private static final DateTimeFormatter YYYY_MM = DateTimeFormatter.ofPattern("yyyyMM");

    @Override
    public YearMonth apply(String input) {
        return Strings.isNullOrEmpty(input) ? null : YearMonth.parse(input, YYYY_MM);
    }

}
