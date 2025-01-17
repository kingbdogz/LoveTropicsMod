package net.tropicraft.lovetropics.client.data;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.IItemProvider;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.tropicraft.lovetropics.Constants;
import net.tropicraft.lovetropics.LoveTropics;
import net.tropicraft.lovetropics.common.Util;

public class TropicraftLangProvider extends LanguageProvider {

    private static class AccessibleLanguageProvider extends LanguageProvider {

        public AccessibleLanguageProvider(DataGenerator gen, String modid, String locale) {
            super(gen, modid, locale);
        }

        @Override
        public void add(String key, String value) {
            super.add(key, value);
        }

        @Override
        protected void addTranslations() {}
    }

    private final AccessibleLanguageProvider upsideDown;

    public TropicraftLangProvider(DataGenerator gen) {
        super(gen, Constants.MODID, "en_us");
        this.upsideDown = new AccessibleLanguageProvider(gen, Constants.MODID, "en_ud");
    }

    @Override
    protected void addTranslations() {}
    
    private String getAutomaticName(Supplier<? extends IForgeRegistryEntry<?>> sup) {
        return Util.toEnglishName(sup.get().getRegistryName().getPath());
    }
    
    private void addBlock(Supplier<? extends Block> block) {
        addBlock(block, getAutomaticName(block));
    }
    
    private void addBlockWithTooltip(Supplier<? extends Block> block, String tooltip) {
        addBlock(block);
        addTooltip(block, tooltip);
    }
    
    private void addBlockWithTooltip(Supplier<? extends Block> block, String name, String tooltip) {
        addBlock(block, name);
        addTooltip(block, tooltip);
    }
    
    private void addItem(Supplier<? extends Item> item) {
        addItem(item, getAutomaticName(item));
    }
    
    private void addItemWithTooltip(Supplier<? extends Item> block, String name, List<String> tooltip) {
        addItem(block, name);
        addTooltip(block, tooltip);
    }
    
    private void addTooltip(Supplier<? extends IItemProvider> item, String tooltip) {
        add(item.get().asItem().getTranslationKey() + ".desc", tooltip);
    }
    
    private void addTooltip(Supplier<? extends IItemProvider> item, List<String> tooltip) {
        for (int i = 0; i < tooltip.size(); i++) {
            add(item.get().asItem().getTranslationKey() + ".desc." + i, tooltip.get(i));
        }
    }
    
    private void add(ItemGroup group, String name) {
        add(group.getTranslationKey(), name);
    }
    
    private void addEntityType(Supplier<? extends EntityType<?>> entity) {
        addEntityType(entity, getAutomaticName(entity));
    }
    
    private void addBiome(Supplier<? extends Biome> biome) {
        addBiome(biome, getAutomaticName(biome));
    }
    
    // Automatic en_ud generation

    private static final String NORMAL_CHARS = 
            /* lowercase */ "abcdefghijklmn\u00F1opqrstuvwxyz" +
            /* uppercase */ "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            /*  numbers  */ "0123456789" +
            /*  special  */ "_,;.?!/\\'";
    private static final String UPSIDE_DOWN_CHARS = 
            /* lowercase */ "\u0250q\u0254p\u01DD\u025Fb\u0265\u0131\u0638\u029E\u05DF\u026Fuuodb\u0279s\u0287n\u028C\u028Dx\u028Ez" +
            /* uppercase */ "\u2C6F\u15FA\u0186\u15E1\u018E\u2132\u2141HI\u017F\u029E\uA780WNO\u0500\u1F49\u1D1AS\u27D8\u2229\u039BMX\u028EZ" +
            /*  numbers  */ "0\u0196\u1105\u0190\u3123\u03DB9\u312586" +
            /*  special  */ "\u203E'\u061B\u02D9\u00BF\u00A1/\\,";
    
    static {
        if (NORMAL_CHARS.length() != UPSIDE_DOWN_CHARS.length()) {
            throw new AssertionError("Char maps do not match in length!");
        }
    }

    private String toUpsideDown(String normal) {
        char[] ud = new char[normal.length()];
        for (int i = 0; i < normal.length(); i++) {
            char c = normal.charAt(i);
            if (c == '%') {
                String fmtArg = "";
                while (Character.isDigit(c) || c == '%' || c == '$' || c == 's' || c == 'd') { // TODO this is a bit lazy
                    fmtArg += c;
                    i++;
                    c = i == normal.length() ? 0 : normal.charAt(i);
                }
                i--;
                for (int j = 0; j < fmtArg.length(); j++) {
                    ud[normal.length() - 1 - i + j] = fmtArg.charAt(j);
                }
                continue;
            }
            int lookup = NORMAL_CHARS.indexOf(c);
            if (lookup >= 0) {
                c = UPSIDE_DOWN_CHARS.charAt(lookup);
            }
            ud[normal.length() - 1 - i] = c;
        }
        return new String(ud);
    }

    @Override
    protected void add(String key, String value) {
        super.add(key, value);
        upsideDown.add(key, toUpsideDown(value));
    }

    @Override
    public void act(DirectoryCache cache) throws IOException {
        super.act(cache);
        upsideDown.act(cache);
    }
}
