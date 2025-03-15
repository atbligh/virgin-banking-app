package com.profdev.bank.data.load;

import com.profdev.bank.data.DataRecord;

import java.util.List;

public interface DataLoader {

    List<DataRecord> retrieveData();
}
