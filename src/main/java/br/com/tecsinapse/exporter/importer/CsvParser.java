/*
 * TecSinapse Exporter
 *
 * License: GNU Lesser General Public License (LGPL), version 3 or later
 * See the LICENSE file in the root directory or <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package br.com.tecsinapse.exporter.importer;

import static br.com.tecsinapse.exporter.importer.Importer.getMappedMethods;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.com.tecsinapse.exporter.CSVUtil;
import br.com.tecsinapse.exporter.annotation.TableCellMapping;
import br.com.tecsinapse.exporter.converter.TableCellConverter;
import br.com.tecsinapse.exporter.converter.group.Default;

class CsvParser<T> implements Parser<T> {

    private final Class<T> clazz;
    private final Class<?> group;
    private List<String> csvLines;
    private int afterLine = Importer.DEFAULT_START_ROW;


    CsvParser(Class<T> clazz, File file, Charset charset, int afterLine, Class<?> group) throws IOException {
        this(clazz, file, charset, group);

        this.afterLine = afterLine;
    }

    CsvParser(Class<T> clazz, InputStream input, Charset charset, int afterLine, Class<?> group) throws IOException {
        this(clazz, input, charset, group);

        this.afterLine = afterLine;
    }

    public CsvParser(Class<T> clazz, List<String> csvLines) {
        this(clazz, csvLines, Default.class);
    }

    public CsvParser(Class<T> clazz, File file, Charset charset) throws IOException {
        this(clazz, file, charset, Default.class);
    }

    public CsvParser(Class<T> clazz, File file, Charset charset, Class<?> group) throws IOException {
        this(clazz, CSVUtil.processCSV(new FileInputStream(file), charset), group);
    }

    public CsvParser(Class<T> clazz, InputStream inputStream, Charset charset) throws IOException {
        this(clazz, inputStream, charset, Default.class);
    }

    public CsvParser(Class<T> clazz, InputStream inputStream, Charset charset, Class<?> group) throws IOException {
        this(clazz, CSVUtil.processCSV(inputStream, charset), group);
    }

    public CsvParser(Class<T> clazz, List<String> csvLines, Class<?> group) {
        this.clazz = clazz;
        this.csvLines = csvLines;
        this.group = group;
    }

    @Override
    public int getNumberOfSheets() {
        return 1;
    }

    @Override
    public void setDateStringPattern(String dateStringPattern) {
    }

    @Override
    public void setDateAsLocalDateTime(boolean considerarLocalDateTime) {
    }

    @Override
    public void setDateTimeStringPattern(String dateTimeStringPattern) {
    }

    /**
     * Não lê a primeira linha
     * <p/>
     *
     * @return
     * @throws Exception
     */
    @Override
    public List<T> parse() throws IllegalAccessException, InstantiationException,
            InvocationTargetException, NoSuchMethodException {
        List<T> list = new ArrayList<>();

        Map<Method, TableCellMapping> cellMappingByMethod = getMappedMethods(clazz, group);

        final Constructor<T> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        for (int i = 0; i < csvLines.size(); i++) {
            final String line = csvLines.get(i);
            if ((i + 1) <= afterLine) {
                continue;
            }

            List<String> fields = split(line);
            T instance = constructor.newInstance();

            for (Entry<Method, TableCellMapping> methodTcm : cellMappingByMethod.entrySet()) {
                Method method = methodTcm.getKey();
                method.setAccessible(true);

                TableCellMapping tcm = methodTcm.getValue();
                String value = getValueOrEmpty(fields, tcm.columnIndex());
                TableCellConverter<?> converter = tcm.converter().newInstance();
                Object obj = converter.apply(value);
                method.invoke(instance, obj);
            }
            list.add(instance);
        }
        return list;
    }

    private String getValueOrEmpty(List<String> fields, int index) {
        if (fields.isEmpty() || fields.size() <= index) {
            return "";
        }
        return fields.get(index);
    }

    private List<String> split(String line) {
        int index = 0;
        int lastIndex = 0;

        List<String> linhaParseadaPorAspas = new ArrayList<>();

        /**
         * Percorre a linha em busca de ;
         * depois verifica se entre 2 ; existem aspas
         * Se houver, é preciso ignorar os ; internos às aspas
         */
        while (lastIndex != -1 && lastIndex < line.length()) {
            index = line.indexOf(";", lastIndex);

            if (index == -1) {
                //ultima coluna
                linhaParseadaPorAspas.add(line.substring(lastIndex).replace(";", ""));
                break;
            } else {
                String coluna = line.substring(lastIndex, index + 1);

                if (temAspas(coluna)) {
                    index = getFinalColuna(line.substring(lastIndex), lastIndex);
                    if (index == -1) {
                        //ultima coluna
                        linhaParseadaPorAspas.add(line.substring(lastIndex).replace("\"\"", "\"").trim());
                        break;
                    }
                    coluna = substringNormalizada(line, lastIndex, index - 1);
                    linhaParseadaPorAspas.add(coluna);
                    lastIndex = index;
                } else {
                    linhaParseadaPorAspas.add(coluna.replace(";", ""));
                    lastIndex = index == -1 ? -1 : index + 1;
                }
            }
        }

        return linhaParseadaPorAspas;
    }

    private int getFinalColuna(String substring, int inicio) {
        char[] chars = substring.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '\"') {
                for (int j = i + 1; j < chars.length; j++) {
                    if (chars[j] == '\"') {
                        return getFinalColuna(substring.substring(j + 1), inicio + j + 1);
                    }
                }
            }

            if (chars[i] == ';') {
                return i + inicio + 1;
            }
        }

        return -1;
    }

    private boolean temAspas(String column) {
        return column.indexOf("\"") != -1;
    }

    private String substringNormalizada(String line, int i, int f) {
        line = line.substring(i, f - 1).trim();
        if (line.startsWith("\"")) {
            line = line.substring(1);
        }
        if (line.endsWith("\"")) {
            line = line.substring(0, line.length() - 1);
        }

        return line.replace("\"\"", "\"").trim();
    }

    @Override
    public void close() throws IOException {
        //nada parser é feito atualmente no construtor
    }
}
