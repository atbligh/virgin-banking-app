package com.profdev.bank.data.load;

import com.opencsv.bean.CsvToBeanBuilder;
import com.profdev.bank.data.DataRecord;
import com.profdev.bank.config.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service(CsvDataLoader.BEAN_ID)
@Slf4j
public class CsvDataLoader implements DataLoader {

    public static final String BEAN_ID = "csv";

    private final AppProperties appProperties;

    private List<DataRecord> dataRecords;

    public CsvDataLoader(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Override
    public List<DataRecord> retrieveData() {
        load();
        return dataRecords;
    }

    private void load() {
        if (dataRecords == null) {
            CsvToBeanBuilder<DataRecord> beanBuilder;
            File dataFile = null;
            try {
                InputStream in = new ClassPathResource(appProperties.dataFile()).getInputStream();
                // TODO not ideal to write temporary file - investigate and change
                //  needed to allow access to the file when running inside a jar
                dataFile = File.createTempFile("transactions-csv-","");
                FileUtils.copyInputStreamToFile(in, dataFile);
                beanBuilder = new CsvToBeanBuilder<>(new FileReader(dataFile));
                beanBuilder.withType(DataRecord.class);
                dataRecords = beanBuilder.build().parse();
            } catch (IOException ex) {
                log.error("Could not parse CSV data file: {}", appProperties.dataFile());
                throw new RuntimeException(ex);
            } finally {
                FileUtils.deleteQuietly(dataFile);
            }
        }
    }
}
