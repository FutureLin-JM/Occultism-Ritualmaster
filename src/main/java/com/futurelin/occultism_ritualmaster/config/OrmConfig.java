package com.futurelin.occultism_ritualmaster.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class OrmConfig {

    public static final ModConfigSpec SERVER_SPEC;
    public static final Server SERVER;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        SERVER = new Server(builder);
        SERVER_SPEC = builder.build();
    }

    public static final class Server {

        public final ModConfigSpec.IntValue ritualmasterInventorySize;
        public final ModConfigSpec.IntValue ritualMinWorkDuration;
        public final ModConfigSpec.DoubleValue ritualDurationScaleFactor;

        Server(ModConfigSpec.Builder builder) {
            builder.push("ritualmaster");

            ritualmasterInventorySize = builder
                    .comment(
                            " 仪式大师的库存容量大小",
                            " 修改该配置对已存在的仪式大师实体不生效",
                            " The storage capacity of Ritualmaster's inventory",
                            " Changing this config does not affect existing Ritualmaster entities"
                    )
                    .defineInRange("ritualmasterInventorySize", 12, 1, 256);

            ritualMinWorkDuration = builder
                    .comment(
                            " 仪式的最小工作时间,单位为 Tick (20 Tick = 1 秒)",
                            " 当 ritualDurationScaleFactor 为 0 时,所有仪式都使用此最小工作时间",
                            " Minimum ritual work duration in ticks (20 ticks = 1 second)",
                            " When ritualDurationScaleFactor is 0, all rituals use this minimum duration")
                    .defineInRange("ritualMinWorkDuration", 20, 1, Integer.MAX_VALUE);

            ritualDurationScaleFactor = builder
                    .comment(
                            " 仪式工作时间缩放系数",
                            " 最终工作时间 = 原配方时间 * 此系数",
                            " 若设为 0,则直接使用 ritualMinWorkDuration 作为工作时间",
                            " Scale factor for ritual work duration",
                            " Final work duration = recipe duration * this factor",
                            " When set to 0, ritualMinWorkDuration is used directly")
                    .defineInRange("ritualDurationScaleFactor", 1.0, 0.0, 100.0);

            builder.pop();
        }
    }
}