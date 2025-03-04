package com.christianoette._A_the_basics._05_steps_in_separate_files.processor;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.christianoette._A_the_basics._05_steps_in_separate_files.dto.InputData;
import com.christianoette._A_the_basics._05_steps_in_separate_files.dto.OutputData;

@Component
@StepScope
public class UpperCaseJsonProcessor implements ItemProcessor<InputData, OutputData> {

    @Value("#{jobParameters['inputPath']}")
    private String inputPath;

    @Override
    public OutputData process(InputData inputData) throws Exception {
        OutputData outputData = new OutputData();
        outputData.value = inputData.value.toUpperCase();
        return outputData;
    }
}
