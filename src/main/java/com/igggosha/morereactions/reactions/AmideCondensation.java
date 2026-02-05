package com.igggosha.morereactions.reactions;

import com.igggosha.morereactions.ExampleMod;
import com.mojang.logging.LogUtils;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.legacy.*;
import com.petrolpark.destroy.chemistry.legacy.genericreaction.DoubleGroupGenericReaction;
import com.petrolpark.destroy.chemistry.legacy.genericreaction.GenericReactant;
import com.petrolpark.destroy.chemistry.legacy.index.DestroyGroupTypes;
import com.petrolpark.destroy.chemistry.legacy.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.legacy.index.group.CarboxylicAcidGroup;
import com.petrolpark.destroy.chemistry.legacy.index.group.PrimaryAmineGroup;
import org.slf4j.Logger;

import java.io.Console;

public class AmideCondensation extends DoubleGroupGenericReaction<CarboxylicAcidGroup, PrimaryAmineGroup>
{
    private static final Logger LOGGER = LogUtils.getLogger();

    public AmideCondensation()
    {
        super(ExampleMod.asResource("amide_condensation"), DestroyGroupTypes.CARBOXYLIC_ACID, DestroyGroupTypes.PRIMARY_AMINE);
    }

    @Override
    public boolean isPossibleIn(ReadOnlyMixture mixture)
    {
        return mixture.getTemperature() >= 100;
    }

    @Override
    public LegacyReaction generateReaction(GenericReactant<CarboxylicAcidGroup> first, GenericReactant<PrimaryAmineGroup> second)
    {
        LegacyMolecularStructure amineStructure = second.getMolecule().shallowCopyStructure();
        PrimaryAmineGroup amineGroup = second.getGroup();

        amineStructure.moveTo(amineGroup.nitrogen)
            .remove(amineGroup.firstHydrogen);

        LegacyMolecularStructure acidStructure = first.getMolecule().shallowCopyStructure();
        CarboxylicAcidGroup acidGroup = first.getGroup();

        acidStructure.moveTo(acidGroup.carbon)
            .remove(acidGroup.proton)
//        acidStructure.moveTo(acidGroup.carbon)
            .remove(acidGroup.alcoholOxygen);

        LegacySpecies amide = moleculeBuilder().structure(LegacyMolecularStructure.joinFormulae(
                acidStructure, amineStructure, LegacyBond.BondType.SINGLE
        )).build();

        return reactionBuilder()
                .addReactant(first.getMolecule(), 1, 1)
                .addReactant(second.getMolecule(), 1, 1)
                .addProduct(amide)
                .addProduct(DestroyMolecules.WATER)
                .build();
    }
}
