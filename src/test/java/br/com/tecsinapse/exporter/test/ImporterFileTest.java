package br.com.tecsinapse.exporter.test;


import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Charsets;
import org.joda.time.LocalDate;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import br.com.tecsinapse.exporter.ExcelType;
import br.com.tecsinapse.exporter.FileType;
import br.com.tecsinapse.exporter.Table;
import br.com.tecsinapse.exporter.TableCell;
import br.com.tecsinapse.exporter.TableCellType;
import br.com.tecsinapse.exporter.annotation.TableCellMapping;
import br.com.tecsinapse.exporter.converter.BigDecimalTableCellConverter;
import br.com.tecsinapse.exporter.converter.IntegerFromBigDecimalTableCellConverter;
import br.com.tecsinapse.exporter.converter.LocalDateTableCellConverter;
import br.com.tecsinapse.exporter.converter.TableCellConverter;
import br.com.tecsinapse.exporter.importer.Importer;

public class ImporterFileTest {

    public static final class LocalDateConverter implements TableCellConverter<LocalDate> {
        @Override
        public LocalDate apply(String input) {
//TODO ExcelParser.dateStringPattern > Importer.dateStringPattern unificar confs em Importer ou ImporterConfigurations para utilizaçao em ExcelParser e CsvParser
//            csv texto puro dd/MM/yyyy
//            xls dd/MM/yyyy 03/01/2014
//            xlsx MM/dd/yyyy 01/03/14
            return null;
        }
    }

    public static final class FileBean {

        private String cidade;
        private String estado;
        private LocalDate data;
        private String vazia;
        private BigDecimal numero;
        private Integer numeroInteger;

        private FileBean() {
        }

        public FileBean(String cidade, String estado, LocalDate data, String vazia, BigDecimal numero, Integer numeroInteger) {
            this.cidade = cidade;
            this.estado = estado;
            //TODO :36
//            this.data = data;
            this.vazia = vazia;
            this.numero = numero;
            this.numeroInteger = numeroInteger;
        }

        @TableCellMapping(columnIndex = 0)
        private void setCidade(String cidade) {
            this.cidade = cidade;
        }

        @TableCellMapping(columnIndex = 1)
        private void setEstado(String estado) {
            this.estado = estado;
        }

        @TableCellMapping(columnIndex = 2, converter = LocalDateConverter.class)
        private void setData(LocalDate data) {
            this.data = data;
        }

        @TableCellMapping(columnIndex = 3)
        private void setVazia(String vazia) {
            this.vazia = vazia;
        }

        @TableCellMapping(columnIndex = 4, converter = BigDecimalTableCellConverter.class)
        private void setNumero(BigDecimal numero) {
            this.numero = numero;
        }

        @TableCellMapping(columnIndex = 4, converter = IntegerFromBigDecimalTableCellConverter.class)
        public void setNumeroInteger(Integer numeroInteger) {
            this.numeroInteger = numeroInteger;
        }
    }

    private File getFile(String name) throws URISyntaxException {
        return new File(getClass().getResource("/files/mock/" + name).toURI());
    }

    @DataProvider(name = "arquivos")
    public Object[][] arquivos() throws URISyntaxException {
        //Cidade;Estado;Data;;Número
        List<FileBean> esperados = new ArrayList<>();
        //Pernambuco;PE;01/01/14;;10
        esperados.add(new FileBean("Pernambuco", "PE", new LocalDate(2014, 1, 1), "", new BigDecimal("10"), 10));
        //Campo Grande;MS;02/01/14;;11
        esperados.add(new FileBean("Campo Grande", "MS", new LocalDate(2014, 1, 2), "", new BigDecimal("11"), 11));
        //Rio de Janeiro;RJ;03/01/14;;12
        esperados.add(new FileBean("Rio de Janeiro", "RJ", new LocalDate(2014, 1, 3), "", new BigDecimal("12"), 12));
        //São Paulo;SP;04/01/14;;13
        esperados.add(new FileBean("São Paulo", "SP", new LocalDate(2014, 1, 4), "", new BigDecimal("13"), 13));
        //São Paulo;SP;05/01/14;;14
        esperados.add(new FileBean("São Paulo", "SP", new LocalDate(2014, 1, 5), "", new BigDecimal("14"), 14));


        return new Object[][]{
                {getFile("planilha.csv"), 1, FileType.CSV, esperados},
                {getFile("planilha.xls"), 1, FileType.XLS, esperados},
                {getFile("planilha.xlsx"), 1, FileType.XLSX, esperados},

                {getFile("planilha.csv"), 3, FileType.CSV, esperados.subList(2, esperados.size())},
                {getFile("planilha.xls"), 3, FileType.XLS, esperados.subList(2, esperados.size())},
                {getFile("planilha.xlsx"), 3, FileType.XLSX, esperados.subList(2, esperados.size())},
        };
    }


    @Test(dataProvider = "arquivos")
    public void validaArquivo(File arquivo, int afterLine, FileType esperadoFileType, List<FileBean> esperados) throws Exception {
        final Importer<FileBean> importer = new Importer<>(FileBean.class, Charsets.UTF_8, arquivo);
        importer.setAfterLine(afterLine);

        assertEquals(importer.getFileType(), esperadoFileType);

        final List<FileBean> beans = importer.parse();
        for (int i = 0; i < beans.size(); i++) {
            final FileBean atual = beans.get(i);
            final FileBean esperado = esperados.get(i);

            assertEquals(atual.cidade, esperado.cidade);
            assertEquals(atual.estado, esperado.estado);
            assertEquals(atual.data, esperado.data);
            assertEquals(atual.vazia, esperado.vazia);
            assertEquals(atual.numero.compareTo(esperado.numero), 0);
            assertEquals(atual.numeroInteger, esperado.numeroInteger);
        }
    }
}