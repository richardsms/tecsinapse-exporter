/*
 * TecSinapse Exporter
 *
 * License: GNU Lesser General Public License (LGPL), version 3 or later
 * See the LICENSE file in the root directory or <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package br.com.tecsinapse.exporter;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import org.apache.poi.ss.util.DateFormatConverter;

import br.com.tecsinapse.exporter.util.Constants;

public class ExporterFormatter {

    public static final ExporterFormatter PT_BR = new ExporterFormatter(Constants.LOCALE_PT_BR);
    public static final ExporterFormatter DEFAULT = new ExporterFormatter(Locale.ENGLISH);

    private final DateTimeFormatter localDateTimeFormat;
    private final DateTimeFormatter localDateFormat;
    private final DateTimeFormatter localTimeFormat;
    private final DecimalFormat decimalFormat;
    private final DecimalFormat integerFormat;
    private final DecimalFormat currencyFormat;
    private final String cellDateTimeFormat;
    private final String cellDateFormat;
    private final String cellTimeFormat;
    private final String cellCurrencyFormat;
    private final String cellDecimalFormat;
    private final String cellIntegerFormat;
    private final Locale locale;

    public ExporterFormatter(String localDateTimeFormat, String localDateFormat, String localTimeFormat, String decimalFormat, String integerFormat, String currencyFormat) {
        this(localDateTimeFormat, localDateFormat, localTimeFormat, decimalFormat, integerFormat, currencyFormat, Locale.getDefault());
    }

    public ExporterFormatter(Locale locale) {
        this.locale = locale;
        this.localDateTimeFormat = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM).withLocale(locale);
        this.localDateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale);
        this.localTimeFormat = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale);
        this.decimalFormat = (DecimalFormat) DecimalFormat.getInstance(locale);
        this.integerFormat = (DecimalFormat) DecimalFormat.getIntegerInstance(locale);
        this.currencyFormat = (DecimalFormat) DecimalFormat.getCurrencyInstance(locale);
        this.cellDateTimeFormat = DateFormatConverter.getJavaDateTimePattern(DateFormat.MEDIUM, locale);
        this.cellDateFormat = DateFormatConverter.getJavaDateTimePattern(DateFormat.MEDIUM, locale);
        this.cellTimeFormat = DateFormatConverter.getJavaDateTimePattern(DateFormat.SHORT, locale);
        this.cellCurrencyFormat = DateFormatConverter.getPrefixForLocale(locale) + currencyFormat.toLocalizedPattern();
        this.cellDecimalFormat = DateFormatConverter.getPrefixForLocale(locale) + decimalFormat.toLocalizedPattern();
        this.cellIntegerFormat = DateFormatConverter.getPrefixForLocale(locale) + integerFormat.toLocalizedPattern();
    }

    public ExporterFormatter(String localDateTimeFormat, String localDateFormat, String localTimeFormat, String decimalFormat, String integerFormat, String currencyFormat, Locale locale) {
        this.locale = locale;
        this.localDateTimeFormat = DateTimeFormatter.ofPattern(localDateTimeFormat, locale);
        this.localDateFormat = DateTimeFormatter.ofPattern(localDateFormat, locale);
        this.localTimeFormat = DateTimeFormatter.ofPattern(localTimeFormat, locale);
        this.decimalFormat = new DecimalFormat(decimalFormat, DecimalFormatSymbols.getInstance(locale));
        this.integerFormat = new DecimalFormat(integerFormat, DecimalFormatSymbols.getInstance(locale));
        this.currencyFormat = new DecimalFormat(currencyFormat, DecimalFormatSymbols.getInstance(locale));
        this.cellDateTimeFormat = DateFormatConverter.convert(locale, localDateTimeFormat);
        this.cellDateFormat = DateFormatConverter.convert(locale, localDateFormat);
        this.cellTimeFormat = DateFormatConverter.convert(locale, localTimeFormat);
        this.cellCurrencyFormat = DateFormatConverter.getPrefixForLocale(locale) + this.currencyFormat.toLocalizedPattern();
        this.cellDecimalFormat = DateFormatConverter.getPrefixForLocale(locale) + this.decimalFormat.toLocalizedPattern();
        this.cellIntegerFormat = DateFormatConverter.getPrefixForLocale(locale) + this.integerFormat.toLocalizedPattern();
    }

    public DateTimeFormatter getLocalDateTimeFormat() {
        return localDateTimeFormat;
    }

    public DateTimeFormatter getLocalDateFormat() {
        return localDateFormat;
    }

    public DateTimeFormatter getLocalTimeFormat() {
        return localTimeFormat;
    }

    public DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    public DecimalFormat getIntegerFormat() {
        return integerFormat;
    }

    public DecimalFormat getCurrencyFormat() {
        return currencyFormat;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public String formatLocalDate(LocalDate localDate) {
        return localDate.format(localDateFormat);
    }

    public String formatLocalTime(LocalTime localTime) {
        return localTime.format(localTimeFormat);
    }

    public String formatLocalDateTime(LocalDateTime localDateTime) {
        return localDateTime.format(localDateTimeFormat);
    }

    public String formatNumber(Number number) {
        if (number instanceof Integer || number instanceof Long) {
            return integerFormat.format(number);
        }
        return decimalFormat.format(number);
    }

    public String formatCurrency(Number number) {
        return currencyFormat.format(number);
    }

    public String formatByType(Object o, boolean isCurrency) {
        if (o instanceof LocalDateTime) {
            return formatLocalDateTime((LocalDateTime) o);
        }
        if (o instanceof LocalDate) {
            return formatLocalDate((LocalDate) o);
        }
        if (o instanceof LocalTime) {
            return formatLocalTime((LocalTime) o);
        }
        if (o instanceof Number) {
            if (isCurrency) {
                return formatCurrency((Number) o);
            }
            return formatNumber((Number) o);
        }
        return o.toString();
    }

    public String getStringFormatByType(Object o, boolean isCurrency) {
        if (o instanceof LocalDateTime) {
            return cellDateTimeFormat;
        }
        if (o instanceof LocalDate) {
            return cellDateFormat;
        }
        if (o instanceof LocalTime) {
            return cellTimeFormat;
        }
        if (o instanceof Integer || o instanceof Long) {
            return cellIntegerFormat;
        }
        if (o instanceof Number && isCurrency) {
            return cellCurrencyFormat;
        }
        if (o instanceof Number) {
            return cellDecimalFormat;
        }
        return null;
    }

}
