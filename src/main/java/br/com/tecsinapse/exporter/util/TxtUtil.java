/*
 * TecSinapse Exporter
 *
 * License: GNU Lesser General Public License (LGPL), version 3 or later
 * See the LICENSE file in the root directory or <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package br.com.tecsinapse.exporter.util;

import java.io.IOException;

import java.time.LocalDateTime;

import br.com.tecsinapse.exporter.servlet.ExportServletUtil;
import br.com.tecsinapse.exporter.txt.FileTxt;

/**
 * This method is moved. It will be removed in version 2.0.0
 *
 * @deprecated use methods from {@link br.com.tecsinapse.exporter.servlet.ExportServletUtil}
 */
@Deprecated
public class TxtUtil {

    public static void exportFile(FileTxt file, String fileName, String chartsetName) throws IOException {
        String filename = String.format("%s_%s", fileName, LocalDateTime.now().format(Constants.DATE_TIME_FILE_NAME));
        ExportServletUtil.facesDownloadFileTxt(file, filename, chartsetName, false);
    }

}
