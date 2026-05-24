package com.futurelin.occultism_ritualmaster.datagen.lang;

import com.futurelin.occultism_ritualmaster.OccultismRitualmaster;
import com.futurelin.occultism_ritualmaster.TranslationKeys;
import com.futurelin.occultism_ritualmaster.registry.OrmEntitiesRegistry;
import com.futurelin.occultism_ritualmaster.registry.OrmItemsRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

public class OrmZHCNProvider extends OrmLangProvider {

    public OrmZHCNProvider(PackOutput output) {
        super(output, "zh_cn");
    }

    @Override
    protected void addTranslations() {
        this.add(TranslationKeys.ITEM_GROUP, "神秘学：仪式大师");
        this.add(OrmEntitiesRegistry.RITUALMASTER.get().getDescriptionId(), "仪式大师");
        this.add("job." + OccultismRitualmaster.MOD_ID + ".ritualmaster", "仪式大师");

        autoDummyFactory(OrmItemsRegistry.RITUAL_DUMMY_SUMMON_RITUALMASTER, "召唤仪式大师", "仪式大师可自动执行仪式。为其提供装有对应仪式五芒星印的§l意外结实的挎包§r，它会拾取周围的材料，并会自行完成仪式。");

        this.add(OrmItemsRegistry.SEALED_PENTACLE.get(), "五芒星印");
        this.add(TranslationKeys.SEALED_PENTACLE_EMPTY, "空");
        this.add(TranslationKeys.SEALED_PENTACLE_TOOLTIP, "右击黄金仪式之碗，将成型的五芒星阵封印在其中。");
        this.add(TranslationKeys.SEALED_PENTACLE_SEALED, "五芒星阵已封印");

        this.add(TranslationKeys.JADE_SHIFT_TOOLTIP, "按住 [§lShift§r] 查看可用五芒星阵");
        this.add(TranslationKeys.JADE_PENTACLE_FOUND, "已发现的五芒星阵：");
        this.add(TranslationKeys.JADE_NO_PENTACLE_FOUND, "未发现五芒星阵");
    }

    @Override
    public void autoDummyFactory(DeferredItem<Item> dummy, String name, String description) {
        this.add(dummy.get(), "仪式：" + name);
        this.addTooltip(dummy.get(), description);
        this.addRitualMessage(dummy, "conditions", "该仪式的部分条件仍未满足。");
        this.addRitualMessage(dummy, "started", "仪式成功完成：" + name +"。");
        this.addRitualMessage(dummy, "finished", "仪式中断：" + name +"。");
        this.addRitualMessage(dummy, "interrupted", "开始进行仪式：" + name +"。");
    }
}
