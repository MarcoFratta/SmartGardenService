package logic;

import model.SmartGarden;

import java.util.List;

public class SmartGardenStrategyImpl implements SmartGardenStrategy{

    private final SmartGarden smartGarden;
    private final List<SmartGardenOperation> operationList;

    public SmartGardenStrategyImpl(final SmartGarden smartGarden,
                                   final List<SmartGardenOperation> operationList) {
        this.smartGarden = smartGarden;
        this.operationList = operationList;
    }


    @Override
    public SmartGarden getOldStatus() {
        return this.smartGarden;
    }

    @Override
    public List<SmartGardenOperation> getOperations() {
        return this.operationList;
    }
}
