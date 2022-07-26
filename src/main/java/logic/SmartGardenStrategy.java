package logic;

import model.SmartGarden;

import java.util.List;

public interface SmartGardenStrategy {
    SmartGarden getOldStatus();
    List<SmartGardenOperation> getOperations();
}
