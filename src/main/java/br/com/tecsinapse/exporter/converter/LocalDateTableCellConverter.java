/*
 * TecSinapse Exporter
 *
 * License: GNU Lesser General Public License (LGPL), version 3 or later
 * See the LICENSE file in the root directory or <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package br.com.tecsinapse.exporter.converter;

import java.time.LocalDate;

public class LocalDateTableCellConverter implements TableCellConverter<LocalDate> {

    @Override
    public LocalDate apply(String input) {
        return input != null && !input.isEmpty() ? LocalDate.parse(input) : null;
    }

}
