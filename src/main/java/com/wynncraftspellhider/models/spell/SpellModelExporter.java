package com.wynncraftspellhider.models.spell;

import com.google.gson.*;
import com.wynncraftspellhider.models.Models;
import com.wynncraftspellhider.models.spell.rules.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;

public class SpellModelExporter {
    public static final int VERSION = 1;
    public static final int SCHEMA_VERSION = 1;

    // Pattern matching names like "bash (3)" or "meteorcircle (11)".
    private static final Pattern NUMBERED = Pattern.compile("^(.+) \\((\\d+)\\)$");

    private enum WynnClass {
        WARRIOR,
        MAGE,
        ARCHER,
        ASSASSIN,
        SHAMAN,
        GLOBAL
    }

    private static final List<SpellConfig> WARRIOR_SPELLS = new ArrayList<>();
    private static final List<SpellConfig> MAGE_SPELLS = new ArrayList<>();
    private static final List<SpellConfig> ARCHER_SPELLS = new ArrayList<>();
    private static final List<SpellConfig> ASSASSIN_SPELLS = new ArrayList<>();
    private static final List<SpellConfig> SHAMAN_SPELLS = new ArrayList<>();
    private static final List<SpellConfig> GLOBAL_SPELLS = new ArrayList<>();

    // Quick lookup map used by export().
    private static final Map<WynnClass, List<SpellConfig>> CLASS_SPELLS = new EnumMap<>(WynnClass.class);

    // Generates Set.of("prefix (1)", "prefix (2)", ..., "prefix (n)")
    private static Set<String> range(String prefix, int from, int to) {
        return IntStream.rangeClosed(from, to)
                .mapToObj(i -> prefix + " (" + i + ")")
                .collect(Collectors.toUnmodifiableSet());
    }

    // spotless:off
    static {
        // WARRIOR
        WARRIOR_SPELLS.add(new SpellConfig("Melee", List.of(
                new SpellGroup("Melee", MatchRule.ofTexture(range("warrior_meleeswipe", 1, 5)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Bash", List.of(
                new SpellGroup("Bash", MatchRule.ofTexture(range("bash", 1, 7)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Uppercut", List.of(
                new SpellGroup("Uppercut", MatchRule.ofTexture(range("uppercut", 1, 7)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("War Scream", List.of(
                new SpellGroup("War scream", MatchRule.ofTexture(range("warscream", 1, 9)))
        )));


        //Paladin
        WARRIOR_SPELLS.add(new SpellConfig("Flaming Uppercut", List.of(
                new SpellGroup("Flaming uppercut", MatchRule.ofTexture(range("flaminguppercut", 1, 8)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Mantle of the Bovemists", List.of(
                new SpellGroup("Mantle", MatchRule.ofTexture("mantle")),
                new SpellGroup("Mantle break", MatchRule.ofTexture(range("mantlebreak", 1, 9)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Shield Strike", List.of(
                new SpellGroup("Shield strike mantle", MatchRule.ofTexture("shieldstrikemantle")),
                new SpellGroup("Mantle attack", MatchRule.ofTexture(range("shieldstrike", 1, 13)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Sparkling Hope", List.of(
                new SpellGroup("Sparkling hope", MatchRule.ofTexture(range("sparklinghope", 1, 5)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Sacred Surge", List.of(
                new SpellGroup("Sacred surge", MatchRule.ofTexture("sacredsurge"))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Emboldening Cry", List.of(
                new SpellGroup("Emboldening cry", MatchRule.ofTexture(range("emboldeningcry", 1, 12)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Radiance", List.of(
                new SpellGroup("radiance", MatchRule.ofTexture(range("radiance", 1, 13)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Heavenly Trumpet", List.of(
                new SpellGroup("Heavenly trumpet", MatchRule.ofTexture("heavenlytrumpet")),
                new SpellGroup("Heavenly trumpet circle", MatchRule.ofTexture(range("heavenlytrumpetcircle", 1, 15)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Second Chance", List.of(
                new SpellGroup("Second chance", MatchRule.ofTexture(range("secondchance", 1, 9)))
        )));

        //todo: check if this interferes with the beams in NOL.
        WARRIOR_SPELLS.add(new SpellConfig("Buried Light",
                "This setting might interfere with the beams in NOL.\n\nPlease let Eshreal know if it does.",
                List.of(
                        new SpellGroup("Buried light", MatchRule.ofTexture("buriedlight"))
                )));

        WARRIOR_SPELLS.add(new SpellConfig("Judgement",
                "To adjust the white border circle please go to the global tab.",
                List.of(
                        new SpellGroup("Judgement sword", MatchRule.ofTexture("judgementsword")),
                        new SpellGroup("Judgement hit circle", MatchRule.ofTexture(range("judgementhitcircle", 1, 2)))
                )));


        //Battle monk
        WARRIOR_SPELLS.add(new SpellConfig("Half-Moon Swipe", List.of(
                new SpellGroup("Half-moon swipe", MatchRule.ofTexture(range("halfmoonswipe", 1, 6))),
                new SpellGroup("Half-moon swipe & flaming uppercut", MatchRule.ofTexture(range("halfmoonswipeflaminguppercut", 1, 5)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Air shout", List.of(
                new SpellGroup("Air shout", MatchRule.ofTexture(range("airshout", 1, 18)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Flying Kick", List.of(
                new SpellGroup("Flying Kick", MatchRule.ofTexture(range("flyingkick", 1, 8)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Whirlwind Strike", List.of(
                new SpellGroup("Whirlwind strike", MatchRule.ofTexture(range("whirlwindstrike", 1, 10))),
                new SpellGroup("Whirlwind strike & flaming uppercut", MatchRule.ofTexture(range("whirlwindstrikeflaminguppercut", 1, 10)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Tempest", List.of(
                new SpellGroup("Tempest", MatchRule.ofTexture(range("tempest", 1, 4)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Cyclone", List.of(
                new SpellGroup("Cyclone", MatchRule.ofTexture("warriorcyclone"))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Thunderclap", List.of(
                new SpellGroup("Thunderclap", MatchRule.ofTexture(range("warriorthunderclap", 1, 2)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Zenith Stance", List.of(
                new SpellGroup("Zenith stance", MatchRule.ofTexture("zenithstance"))
        )));


        // Fallen
        WARRIOR_SPELLS.add(new SpellConfig("Fireworks", List.of(
                new SpellGroup("Fireworks", MatchRule.ofTexture(range("warriorfireworks", 1, 7)))
        )));

        //todo: maybe add more configuration to the jaw of the skull and circle.
        WARRIOR_SPELLS.add(new SpellConfig("Bak'al's Grasp", List.of(
                new SpellGroup("Bak'al's grasp", MatchRule.ofTexture(range("bakalsgrasp", 1, 2)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Boiling Blood", List.of(
                new SpellGroup("Boiling blood", MatchRule.ofTexture("warriorboilingblood"))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Bloodlust", List.of(
                new SpellGroup("Bloodlust", MatchRule.ofTexture(range("warriorbloodlust", 1, 9)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Bloodied Armory", List.of(
                new SpellGroup("Bloodied armory", MatchRule.ofTexture(range("bloodiedarmory", 1, 6)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Beyond Salvation", List.of(
                new SpellGroup("Beyond salvation", MatchRule.ofTexture(range("beyondsalvation", 1, 12)))
        )));









        // MAGE
        MAGE_SPELLS.add(new SpellConfig("Melee", List.of(
                new SpellGroup("Melee", MatchRule.ofTexture("meleebeam"))
        )));

        MAGE_SPELLS.add(new SpellConfig("Meteor",
                "Meteor explosion and archer traps explosion use identical models. Editing this will modify traps too.",
                List.of(
                        new SpellGroup("Meteor trail", MatchRule.ofTexture("meteor", Set.of("4aa761cc79b8e1a17d786253ef423aca6bb92cd65f5afba467a6c38d36662702", "5559c862acd173f8f333eb409634cd6f5b7582e53a15013b98cb19348ea3c123", "fecc509fe5d212d14d9cf3bf93b339c80076f33cd7cc8254b0a93b8beeef0927"))),
                        new SpellGroup("Meteor ball",MatchRule.ofTexture("meteor", Set.of("4918908e2c891617ee71ca135251d6e17c610e40e7aa47670fd449b9a8dd3518"))),
                        new SpellGroup("Meteor circle", MatchRule.ofTexture(range("meteorcircle", 1, 11))),
                        new SpellGroup("Meteor explosion", MatchRule.ofTexture(range("meteorexplosion", 1, 16))),
                        new SpellGroup("Meteor skycircle", MatchRule.ofTexture(range("meteorskycircle", 1, 6))),
                        new SpellGroup("Meteor beam", MatchRule.ofTexture("meteorbeam"))
                )));

        MAGE_SPELLS.add(new SpellConfig("Heal", List.of(
                new SpellGroup("Heal circle", MatchRule.ofTexture(range("heal", 1, 7)))
        )));

        MAGE_SPELLS.add(new SpellConfig("Ice snake", List.of(
                new SpellGroup("Ice snake", MatchRule.ofTexture("icesnake"))
        )));

        MAGE_SPELLS.add(new SpellConfig("Teleport", List.of(
                new SpellGroup("Teleport beam", MatchRule.ofTexture("teleport"))
        )));


        //Arcanist
        MAGE_SPELLS.add(new SpellConfig("Burning Sigil", List.of(
                new SpellGroup("Burning sigil circle", MatchRule.ofTexture(range("burningsigilcircle", 1, 2))),
                new SpellGroup("Burning sigil emblem", MatchRule.ofTexture(range("burningsigilemblem", 1, 10))),
                new SpellGroup("Burning sigil text", MatchRule.ofTexture(range("burningsigiltext", 1, 11)))
        )));

        MAGE_SPELLS.add(new SpellConfig("Arcane Transfer", List.of(
                new SpellGroup("Arcane transfer", MatchRule.ofTexture(range("arcanetransfer", 1, 10)))
        )));

        MAGE_SPELLS.add(new SpellConfig("Pyrokinesis", List.of(
                new SpellGroup("Pyrokinesis", MatchRule.ofTexture(range("pyrokinesis", 1, 9)))
        )));

        MAGE_SPELLS.add(new SpellConfig("Judrajim", List.of(
                new SpellGroup("Judrajim", MatchRule.ofTexture("judrajim"))
        )));

        MAGE_SPELLS.add(new SpellConfig("Thunderstorm",
                "Adjusting this will also effect the thunderbolts from Angelic Ascension (boltslinger ultimate) and from Sundered Skies (ritualist ultimate).\n\nThey use the same textures.",
                List.of(
                        new SpellGroup("Thunderstorm", MatchRule.ofTexture(range("thunderstorm", 1, 10)))
                )));

        MAGE_SPELLS.add(new SpellConfig("Twisted Origin", List.of(
                new SpellGroup("Red dragon", MatchRule.ofTexture("reddragon")),
                new SpellGroup("Yellow dragon", MatchRule.ofTexture("yellowdragon")),
                new SpellGroup("Yellow dragon beam", MatchRule.ofTexture(range("yellowdragonbeam", 1, 41)))
        )));




        //Riftwalker
        MAGE_SPELLS.add(new SpellConfig("Displacement", List.of(
                new SpellGroup("Displacement", MatchRule.ofTexture("magedisplacement"))
        )));

        MAGE_SPELLS.add(new SpellConfig("Astral Fragmentation", List.of(
                new SpellGroup("Astral fragmentation", MatchRule.ofTexture("astralfragmentation"))
        )));

        MAGE_SPELLS.add(new SpellConfig("Frozen Tornado", List.of(
                new SpellGroup("Frozen Tornado", MatchRule.ofTexture("frozentornado"))
        )));

        MAGE_SPELLS.add(new SpellConfig("Vacuokinesis", List.of(
                new SpellGroup("Vacuokinesis", MatchRule.ofTexture(range("vacuokinesis", 1, 5)))
        )));

        MAGE_SPELLS.add(new SpellConfig("Dimensional Tear",
                "To adjust the purple fireballs that come from this go to the global spells. It's in Fireballs",
                List.of(
                        new SpellGroup("Dimensional tear", MatchRule.ofTexture(range("dimensionaltear", 1, 25)))
                )));

        MAGE_SPELLS.add(new SpellConfig("Time Vortex", List.of(
                new SpellGroup("Time vortex", MatchRule.ofTexture("timevortex"))
        )));



        //Lightbender
        MAGE_SPELLS.add(new SpellConfig("Sunshower", List.of(
                new SpellGroup("Sunshower", MatchRule.ofTexture(range("sunshower", 1, 9)))
        )));

        MAGE_SPELLS.add(new SpellConfig("Ophanim & Lightweaver", List.of(
                new SpellGroup("Ophanim & Lightweaver balls", MatchRule.ofTexture(range("ophanimballs", 1, 10))),
                new SpellGroup("Ophanim back", MatchRule.ofTexture("ophanimback"))
        )));

        MAGE_SPELLS.add(new SpellConfig("Freezing Sigil", List.of(
                new SpellGroup("Freezing sigil", MatchRule.ofTexture(range("freezingsigil", 1, 13)))
        )));

        MAGE_SPELLS.add(new SpellConfig("Sunflare", List.of(
                new SpellGroup("Sunflare", MatchRule.ofTexture(range("sunflare", 1, 12)))
        )));

        MAGE_SPELLS.add(new SpellConfig("Dawn",
                "To adjust the fireballs that come from this go to the global spells. It's in Fireballs",
                List.of(
                        new SpellGroup("Sun", MatchRule.ofTexture("sun"))
                )));









        // ARCHER
        ARCHER_SPELLS.add(new SpellConfig("Arrows",
                "Affects all arrows rendered in the world that are arrow entities.\n\nThis includes arrows fired by the player, other players, and mobs.\n\nThe transparency on the arrows is currently not implemented.",
                List.of(
                        new SpellGroup("Arrows", MatchRule.ofEntityType("arrow"))
                )));

        ARCHER_SPELLS.add(new SpellConfig("Arrow Bomb",
                "Arrow bomb explosion will also effect exploding puppets.\n\nThey use the same textures.",
                List.of(
                        new SpellGroup("Arrow bomb explosion", MatchRule.ofTexture(range("arrowbombexplosion", 1, 8)))
                )));

        ARCHER_SPELLS.add(new SpellConfig("Escape", List.of(
                new SpellGroup("Escape", MatchRule.ofTexture(range("archerescape", 1, 5)))
        )));

        ARCHER_SPELLS.add(new SpellConfig("Arrow Shield", List.of(
                new SpellGroup("Arrow shield", MatchRule.ofTexture("arrowshield"))
        )));

        ARCHER_SPELLS.add(new SpellConfig("Arrow Storm", List.of(
                new SpellGroup("Arrow storm circle", MatchRule.ofTexture(range("arrowstormcircle", 1, 13)))
        )));

        //Sharpshooter
        ARCHER_SPELLS.add(new SpellConfig("Phantom Ray", List.of(
                new SpellGroup("Phantom ray", MatchRule.ofTexture("phantomray"))
        )));

        ARCHER_SPELLS.add(new SpellConfig("Twain's Arc", List.of(
                new SpellGroup("Twain's arc bow", MatchRule.ofTexture("twainsarcbow")),
                new SpellGroup("Twain's arc shot", MatchRule.ofTexture(range("twainsarcshot", 1, 11)))
        )));

        ARCHER_SPELLS.add(new SpellConfig("Crepuscular Ray", List.of(
                new SpellGroup("Crepuscular ray", MatchRule.ofTexture(range("crepuscularray", 1, 6)))
        )));

        ARCHER_SPELLS.add(new SpellConfig("Repel", List.of(
                new SpellGroup("Repel", MatchRule.ofTexture(range("archerrepel", 1, 6)))
        )));

        ARCHER_SPELLS.add(new SpellConfig("The Twilight Zone", List.of(
                new SpellGroup("The twilight zone bow", MatchRule.ofTexture("thetwilightzonebow")),
                new SpellGroup("The twilight zone shot", MatchRule.ofTexture(range("thetwilightzoneshot", 1, 15)))
        )));

        // Trapper
        ARCHER_SPELLS.add(new SpellConfig("Bryophyte Roots", List.of(
                new SpellGroup("Bryophyte Roots", MatchRule.ofTexture("bryophyteroots"))
        )));

        //the "Arming name tag (all)" spellgroup has own special handling in clientpacketlistenermixin, so if you change the name you have to change it there too.
        ARCHER_SPELLS.add(new SpellConfig("Basaltic Trap",
                "The transparency on the basaltic trap model is not implemented.\n\nThis is because this still uses the old armor stand way of making models.",
                List.of(
                        new SpellGroup("Basaltic trap", MatchRule.ofArmorStand(ArmorStandRule.ArmorStandModel.TRAP_0, ArmorStandRule.ArmorStandModel.TRAP_1)),
                        new SpellGroup("% Damage name tag (all)", MatchRule.ofTextDisplay("^\\+\\d{1,3}% Damage$", TextDisplayRule.OwnerFilter.ALL)),
                        new SpellGroup("Arming name tag (all)", MatchRule.ofTextDisplay("^Arming\\.\\.\\.$", TextDisplayRule.OwnerFilter.ALL))
                )));

        ARCHER_SPELLS.add(new SpellConfig("Murder Flock",
                "The transparency on the Crow model is not implemented.\n\nThis is because this still uses the old armor stand way of making models.",
                List.of(
                        new SpellGroup("Crow", MatchRule.ofArmorStand(ArmorStandRule.ArmorStandModel.CROW_BODY, ArmorStandRule.ArmorStandModel.CROW_WING_LEFT, ArmorStandRule.ArmorStandModel.CROW_WING_RIGHT)),
                        new SpellGroup("Crow name tag (yours)", MatchRule.ofTextDisplay(".*('s|') Crow\n.*", TextDisplayRule.OwnerFilter.LOCAL_PLAYER)),
                        new SpellGroup("Crow name tag (others)", MatchRule.ofTextDisplay(".*('s|') Crow\n.*", TextDisplayRule.OwnerFilter.OTHER_PLAYERS))
                )));

        ARCHER_SPELLS.add(new SpellConfig("Ivyroot Mamba",
                "The transparency on the Ivyroot Mamba model is not implemented.\n\nThis is because this still uses the old armor stand way of making models.",
                List.of(
                        new SpellGroup("Mamba", MatchRule.ofArmorStand(ArmorStandRule.ArmorStandModel.MAMBA_HEAD, ArmorStandRule.ArmorStandModel.MAMBA_BODY, ArmorStandRule.ArmorStandModel.MAMBA_BUSH)),
                        new SpellGroup("Snake name tag (yours)", MatchRule.ofTextDisplay(".*('s|') Snake\n.*", TextDisplayRule.OwnerFilter.LOCAL_PLAYER)),
                        new SpellGroup("Snake mamba name tag (others)", MatchRule.ofTextDisplay(".*('s|') Snake\n.*", TextDisplayRule.OwnerFilter.OTHER_PLAYERS))
                )));


        ARCHER_SPELLS.add(new SpellConfig("Call of the Hound", List.of(
                new SpellGroup("Call of the hound", MatchRule.ofTexture("callofthehound")),
                new SpellGroup("Hound name tag (yours)", MatchRule.ofTextDisplay(".*('s|') Hound\n.*", TextDisplayRule.OwnerFilter.LOCAL_PLAYER)),
                new SpellGroup("Hound name tag (others)", MatchRule.ofTextDisplay(".*('s|') Hound\n.*", TextDisplayRule.OwnerFilter.OTHER_PLAYERS))
        )));

        ARCHER_SPELLS.add(new SpellConfig("Chilling Snare", List.of(
                new SpellGroup("Chilling Snare", MatchRule.ofTexture("chillingsnare"))
        )));

        ARCHER_SPELLS.add(new SpellConfig("Snow Storm", List.of(
                new SpellGroup("Snow storm", MatchRule.ofTexture("snowstorm"))
        )));


        ARCHER_SPELLS.add(new SpellConfig("Extinction Event", List.of(
                new SpellGroup("Extinction event bomb", MatchRule.ofTexture("extinctioneventbomb")),
                new SpellGroup("Extinction event bomb fire", MatchRule.ofTexture(range("extinctioneventbombfire", 1, 3))),
                new SpellGroup("Extinction event crow", MatchRule.ofTexture("extinctioneventcrow"))
        )));

        //Boltslinger
        ARCHER_SPELLS.add(new SpellConfig("Guardian Angels", List.of(
                new SpellGroup("Guardian angels", MatchRule.ofTexture("guardianangels"))
        )));

        ARCHER_SPELLS.add(new SpellConfig("Arsenal Synergy",
                "This is a global toggle for all flint entities.\n\nIn my testing it does not effect ingredients that look like flint.\n\nTransparency does not work with this for now.",
                List.of(
                        new SpellGroup("Arsenal synergy flint arrow", MatchRule.ofItemEntity(Items.FLINT))
                )));

        ARCHER_SPELLS.add(new SpellConfig("Fierce Stomp", List.of(
                new SpellGroup("Fierce stomp", MatchRule.ofTexture(range("fiercestomp", 1, 9)))
        )));

        //Judgement uses the exact same texture as this. It's the fully white circles. We use model hashes.
        ARCHER_SPELLS.add(new SpellConfig("Courier's Edict", List.of(
                new SpellGroup("Courier's edict", MatchRule.ofTexture("judgementwhitecircle", Set.of("2bafb54591222834561f8d43b5a60b1959e76e2a0e3c4866333c1b7b1677041c")))
        )));

        //I tried looking into the hashes. but the hashes are also the same. they do have different modelids and 1 is always 2 lower than thunderstorm.
        ARCHER_SPELLS.add(new SpellConfig("Angelic Ascension",
                "To adjust the thunderstorm from the arrow bomb please adjust the mage ability.\n\nThey use they same textures.",
                List.of(
                        new SpellGroup("Angelic ascension melee", MatchRule.ofTexture("angelicascensionmelee")),
                        new SpellGroup("Angelic ascension guardian angel", MatchRule.ofTexture("angelicascensionguardianangel")),
                        new SpellGroup("Angelic ascension wings", MatchRule.ofTexture(range("angelicascensionwings", 1, 2)))
                )));






        // ASSASSIN
        ASSASSIN_SPELLS.add(new SpellConfig("Melee", List.of(
                new SpellGroup("Melee", MatchRule.ofTexture(range("assassinmelee", 1, 8)))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Spin Attack", List.of(
                new SpellGroup("Spin attack", MatchRule.ofTexture(range("spinattack", 1, 7)))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Multi Hit", List.of(
                new SpellGroup("Multi hit swipe", MatchRule.ofTexture(range("multihitswipe", 1, 6))),
                new SpellGroup("Multi hit enemy", MatchRule.ofTexture(range("multihitenemy", 1, 2)))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Smoke Bomb", List.of(
                new SpellGroup("Smoke bomb", MatchRule.ofTexture("smokebomb"))
        )));

        // Acrobat
        ASSASSIN_SPELLS.add(new SpellConfig("Ripple", List.of(
                new SpellGroup("Ripple",
                        MatchRule.ofTexture(range("assassinripple", 1, 14)))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Lacerate", List.of(
                new SpellGroup("Lacerate", MatchRule.ofTexture(range("lacerate", 1, 8)))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Swan Dive", List.of(
                new SpellGroup("Swan dive", MatchRule.ofTexture(range("swandiveripple", 1, 4)))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Shurikens", List.of(
                new SpellGroup("Shurikens", MatchRule.ofTexture("shurikens"))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Jasmine Bloom", List.of(
                new SpellGroup("Small jasmine bloom", MatchRule.ofTexture("smalljasminebloom")),
                new SpellGroup("Big jasmine bloom", MatchRule.ofTexture("bigjasminebloom"))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Petal Storm", List.of(
                new SpellGroup("Petal Storm", MatchRule.ofTexture(range("petalstorm", 1, 4)))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Serpent's Garden", List.of(
                new SpellGroup("Serpent's garden flowers", MatchRule.ofTexture(range("serpentsgardenflowers", 1, 4)))
        )));

        // Trickster
        //todo: maybe add support for clones. they appear to be Type: entity.minecraft.player. but not sure. looked for 2 seconds.
        ASSASSIN_SPELLS.add(new SpellConfig("Bamboozle", List.of(
                new SpellGroup("Bamboozle", MatchRule.ofTexture(range("bamboozle", 1, 8)))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Vanish", List.of(
                new SpellGroup("Vanish", MatchRule.ofTexture(range("vanish", 1, 10)))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Malicious Mockery", List.of(
                new SpellGroup("Malicious mockery", MatchRule.ofTexture(range("maliciousmockery", 1, 10)))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Misdirection", List.of(
                new SpellGroup("Misdirection", MatchRule.ofTexture("confusedstar"))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Toxic Sludge", List.of(
                new SpellGroup("Toxic sludge", MatchRule.ofTexture(range("toxicsludge", 1, 16)))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Deflagrate", List.of(
                new SpellGroup("Deflagrate", MatchRule.ofTexture("deflagrate"))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Noxious Haze", List.of(
                new SpellGroup("Noxious haze smoke bomb", MatchRule.ofTexture("noxioushazesmokebomb"))
        )));

        // Shadestepper
        ASSASSIN_SPELLS.add(new SpellConfig("Backstab", List.of(
                new SpellGroup("Backstab", MatchRule.ofTexture(range("backstab", 1, 4)))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Surprise Strike", List.of(
                new SpellGroup("Surprise strike", MatchRule.ofTexture(range("surprisestrike", 1, 6)))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Black Hole", List.of(
                new SpellGroup("Black hole smoke bomb", MatchRule.ofTexture(range("blackholesmokebomb", 1, 9)))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Violent Vortex", List.of(
                new SpellGroup("Violent vortex", MatchRule.ofTexture(range("violentvortex", 1, 10)))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Marked", List.of(
                new SpellGroup("Marked", MatchRule.ofTexture("assassinmarked"))
        )));

        //todo: add support for the knives at the back of the player. they have custom model data but it's inside an armor stand.
        ASSASSIN_SPELLS.add(new SpellConfig("Nightcloak Knives", List.of(
                new SpellGroup("Nightcloak knives", MatchRule.ofTexture(range("nightcloakknivesattack", 1, 7)))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Satsujin", List.of(
                new SpellGroup("Satsujin", MatchRule.ofTexture(range("satsujin", 1, 10)))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Pierce the Veil", List.of(
                new SpellGroup("Pierce the veil", MatchRule.ofTexture("piercetheveil"))
        )));





        // SHAMAN
        SHAMAN_SPELLS.add(new SpellConfig("Melee", List.of(
                new SpellGroup("Melee", MatchRule.ofTexture(range("shamanmelee", 1, 4)))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Totem", List.of(
                new SpellGroup("Totem", MatchRule.ofTexture(range("shamantotem", 1, 2))),
                new SpellGroup("Totem circle", MatchRule.ofTexture("shamantotemcircle")),
                new SpellGroup("Totem effects", MatchRule.ofTexture(range("shamantotemeffects", 1, 4))),
                new SpellGroup("Totem name tag (yours)", MatchRule.ofTextDisplay(".*('s|') Totem\n.*", TextDisplayRule.OwnerFilter.LOCAL_PLAYER)),
                new SpellGroup("Totem name tag (others)", MatchRule.ofTextDisplay(".*('s|') Totem\n.*", TextDisplayRule.OwnerFilter.OTHER_PLAYERS))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Totemic Smash", List.of(
                new SpellGroup("Totemic smash", MatchRule.ofTexture(range("totemicsmash", 1, 13)))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Aura", List.of(
                new SpellGroup("Aura", MatchRule.ofTexture("shamanaura"))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Uproot", List.of(
                new SpellGroup("Uproot", MatchRule.ofTexture(range("shamanuproot", 1, 5)))
        )));

        //Acolyte
        SHAMAN_SPELLS.add(new SpellConfig("Sacrificial Shrine", List.of(
                new SpellGroup("Sacrificial shrine aura", MatchRule.ofTexture("sacrificialshrineaura"))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Eldritch Call", List.of(
                new SpellGroup("Eldritch call tentacle", MatchRule.ofTexture("eldritchcalltentacle")),
                new SpellGroup("Eldritch call tentacle hit", MatchRule.ofTexture(range("eldritchcalltentaclehit", 1, 4)))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Blood Sorrow", List.of(
                new SpellGroup("Blood sorrow square", MatchRule.ofTexture(range("bloodsorrowsquare", 1, 8))),
                new SpellGroup("Blood sorrow effects", MatchRule.ofTexture(range("bloodsorroweffects", 1, 21)))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Moment to Gloom", List.of(
                new SpellGroup("Moment to gloom totem", MatchRule.ofTexture(range("monumenttogloomtotem", 1, 2)))
        )));

        //Ritualist
        SHAMAN_SPELLS.add(new SpellConfig("Rain Dance", List.of(
                new SpellGroup("Rain dance cloud", MatchRule.ofTexture("raindancecloud")),
                new SpellGroup("Rain dance rain", MatchRule.ofTexture(range("raindancerain", 1, 4)))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Frog Dance", List.of(
                new SpellGroup("Frog dance bounce", MatchRule.ofTexture(range("frogdancebounce", 1, 10)))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Totemic Shatter", List.of(
                new SpellGroup("Totemic shatter", MatchRule.ofTexture(range("totemicshatter", 1, 9)))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Tribal Chants", List.of(
                new SpellGroup("Tribal chant lunatic", MatchRule.ofTexture(range("tribalchantlunatic", 1, 8))),
                new SpellGroup("Tribal chant fanatic", MatchRule.ofTexture(range("tribalchantfanatic", 1, 8))),
                new SpellGroup("Tribal chant heretic", MatchRule.ofTexture(range("tribalchantheretic", 1, 10)))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Awakened", List.of(
                new SpellGroup("Awakened circle", MatchRule.ofTexture("awakenedcircle")),
                new SpellGroup("Awakened effects", MatchRule.ofTexture(range("awakenedeffects", 1, 13)))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Pool of Rejuvination", List.of(
                new SpellGroup("Pool of rejuvination circle", MatchRule.ofTexture("poolofrejuvinationcircle")),
                new SpellGroup("Pool of rejuvination sparkles", MatchRule.ofTexture("poolofrejuvinationsparkles"))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Sundered Skies",
                "If you want to configure the thunderstorm effects. Please configure the mage ability.",
                List.of(
                        new SpellGroup("Sundered skies cloud", MatchRule.ofTexture("sunderedskiescloud")),
                        new SpellGroup("Sundered skies circles", MatchRule.ofTexture("sunderedskiescircles"))
                )));

        //Summoner
        SHAMAN_SPELLS.add(new SpellConfig("Puppet Master", List.of(
                new SpellGroup("Puppet", MatchRule.ofTexture("puppetmasterpuppet")),
                new SpellGroup("Puppet knife", MatchRule.ofTexture("puppetmasterknife")),
                new SpellGroup("Puppet name tag (yours)", MatchRule.ofTextDisplay(".*('s|') Puppet.*", TextDisplayRule.OwnerFilter.LOCAL_PLAYER)),
                new SpellGroup("Puppet name tag (others)", MatchRule.ofTextDisplay(".*('s|') Puppet.*", TextDisplayRule.OwnerFilter.OTHER_PLAYERS))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Crimson Effigy", List.of(
                new SpellGroup("Crimson effigy", MatchRule.ofTexture("crimsoneffigy")),
                new SpellGroup("Crimson effigy name tag (yours)", MatchRule.ofTextDisplay(".*('s|') Effigy", TextDisplayRule.OwnerFilter.LOCAL_PLAYER)),
                new SpellGroup("Crimson effigy name tag (others)", MatchRule.ofTextDisplay(".*('s|') Effigy", TextDisplayRule.OwnerFilter.OTHER_PLAYERS))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Commander", List.of(
                new SpellGroup("Commander puppet rally knife", MatchRule.ofTexture("commanderpuppetrallyknife"))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Hummingbird's Song", List.of(
                new SpellGroup("Hummingbird carving", MatchRule.ofTexture("hummingbirdcarving")),
                new SpellGroup("Hummingbird name tag (yours)", MatchRule.ofTextDisplay(".*('s|') Bird", TextDisplayRule.OwnerFilter.LOCAL_PLAYER)),
                new SpellGroup("Hummingbird name tag (others)", MatchRule.ofTextDisplay(".*('s|') Bird", TextDisplayRule.OwnerFilter.OTHER_PLAYERS))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Invigorating Wave", List.of(
                new SpellGroup("Invigorating wave aura", MatchRule.ofTexture("invigoratingwaveaura"))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Patchwork Abomination", List.of(
                new SpellGroup("Patchwork abomination", MatchRule.ofTexture("patchworkabomination")),
                new SpellGroup("Patchwork abomination name tag (yours)", MatchRule.ofTextDisplay(".*('s|') Patchwork Abomination\n.*", TextDisplayRule.OwnerFilter.LOCAL_PLAYER)),
                new SpellGroup("Patchwork abomination name tag (others)", MatchRule.ofTextDisplay(".*('s|') Patchwork Abomination\n.*", TextDisplayRule.OwnerFilter.OTHER_PLAYERS))
        )));





        //GLOBAL
        GLOBAL_SPELLS.add(new SpellConfig("Mob totem & Judgement border",
                "This setting adjusts the mob totem & Judgement border circle.\n\nThey use the same textures.",
                List.of(
                        new SpellGroup("Mob totem & Judgement border", MatchRule.ofTexture("totembordercircle"))
                )));

        GLOBAL_SPELLS.add(new SpellConfig("NOTG & Judgement Shockwave",
                "This setting adjusts the Grootslang Wyrmling's shockwave and the Judgement Shockwave.\n\nThey use the same textures.",
                List.of(
                        new SpellGroup("NOTG & Judgement Shockwave", MatchRule.ofTexture("judgementwhitecircle", Set.of("0d547f7ab5d93aab99699369054910576bab7388ecfa6c69e56a57dd6be88ac1")))
                )));

        GLOBAL_SPELLS.add(new SpellConfig("Fireballs",
                "This setting adjusts Dawn's fireball and Dimensional Tear's purple fireball.\n\nThis setting also effects the fireballs in NOL during the sun and black hole attacks.",
                List.of(
                        new SpellGroup("Fireball (NOL & Dawn)", MatchRule.ofTexture("fireball")),
                        new SpellGroup("Purple fireball (NOL & Dimensional tear)", MatchRule.ofTexture("purplefireball"))
                )));

        GLOBAL_SPELLS.add(new SpellConfig("Ultimate Charged", List.of(
                new SpellGroup("Ultimate charged", MatchRule.ofTexture(range("ultimatecharged", 1, 16)))
        )));

        GLOBAL_SPELLS.add(new SpellConfig("Quake", List.of(
                new SpellGroup("Quake", MatchRule.ofTexture(range("quakespecial", 1, 8)))
        )));

        GLOBAL_SPELLS.add(new SpellConfig("Chain lightning", List.of(
                new SpellGroup("Chain lightning", MatchRule.ofTexture(range("chainlightningspecial", 1, 5)))
        )));

        GLOBAL_SPELLS.add(new SpellConfig("Curse", List.of(
                new SpellGroup("Curse", MatchRule.ofTexture(range("cursespecial", 1, 21)))
        )));

        GLOBAL_SPELLS.add(new SpellConfig("Courage", List.of(
                new SpellGroup("Courage", MatchRule.ofTexture(range("couragespecial", 1, 9)))
        )));

        GLOBAL_SPELLS.add(new SpellConfig("Wind Prison", List.of(
                new SpellGroup("Wind prison", MatchRule.ofTexture(range("windprisonspecial", 1, 6)))
        )));



        // Build the lookup map.
        CLASS_SPELLS.put(WynnClass.WARRIOR, WARRIOR_SPELLS);
        CLASS_SPELLS.put(WynnClass.MAGE, MAGE_SPELLS);
        CLASS_SPELLS.put(WynnClass.ARCHER, ARCHER_SPELLS);
        CLASS_SPELLS.put(WynnClass.ASSASSIN, ASSASSIN_SPELLS);
        CLASS_SPELLS.put(WynnClass.SHAMAN, SHAMAN_SPELLS);
        CLASS_SPELLS.put(WynnClass.GLOBAL, GLOBAL_SPELLS);
    }
    // spotless:on

    public static void exportToFile() {
        exportToFile(Models.configModel.configFolder.toPath());
    }

    private static void exportToFile(Path configDir) {
        try {
            Files.createDirectories(configDir);
            Path out = configDir.resolve("spell_registry.json");
            String json = export();
            Files.writeString(out, json, StandardCharsets.UTF_8);
            System.out.println("[SpellModelExporter] Written to: " + out.toAbsolutePath());
        } catch (Exception e) {
            System.err.println("[SpellModelExporter] Failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String export() {
        JsonObject root = new JsonObject();
        root.addProperty("version", VERSION);
        root.addProperty("schemaVersion", SCHEMA_VERSION);

        JsonObject classes = new JsonObject();
        for (WynnClass wynnClass : WynnClass.values()) {
            classes.add(wynnClass.name(), serializeSpellConfigs(CLASS_SPELLS.get(wynnClass)));
        }
        root.add("classes", classes);

        return new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create()
                .toJson(root);
    }

    private static JsonArray serializeSpellConfigs(List<SpellConfig> configs) {
        JsonArray array = new JsonArray();
        for (SpellConfig config : configs) {
            array.add(serializeSpellConfig(config));
        }
        return array;
    }

    private static JsonObject serializeSpellConfig(SpellConfig config) {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", config.name);
        if (config.description != null) {
            obj.addProperty("description", config.description);
        }
        JsonArray groups = new JsonArray();
        for (SpellGroup group : config.groups) {
            groups.add(serializeSpellGroup(group));
        }
        obj.add("groups", groups);
        return obj;
    }

    private static JsonObject serializeSpellGroup(SpellGroup group) {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", group.name);
        if (group.description != null) {
            obj.addProperty("description", group.description);
        }
        JsonArray rules = new JsonArray();
        for (MatchRule rule : group.rules) {
            JsonObject serialized = serializeMatchRule(rule);
            if (serialized != null) rules.add(serialized);
        }
        obj.add("rules", rules);
        return obj;
    }

    private static JsonObject serializeMatchRule(MatchRule rule) {
        return switch (rule) {
            case TextureRule tr -> serializeTextureRule(tr);
            case EntityTypeRule etr -> serializeEntityTypeRule(etr);
            case TextDisplayRule tdr -> serializeTextDisplayRule(tdr);
            case ArmorStandRule asr -> serializeArmorStandRule(asr);
            case ItemEntityRule ier -> serializeItemEntityRule(ier);
        };
    }

    private static JsonObject serializeTextureRule(TextureRule rule) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "texture");
        obj.add("names", serializeNames(rule.textureNames));
        if (rule.hasFingerprints()) {
            JsonArray fps = new JsonArray();
            new TreeSet<>(rule.fingerprints).forEach(fps::add);
            obj.add("fingerprints", fps);
        }
        return obj;
    }

    private static JsonArray serializeNames(Set<String> names) {
        Map<String, List<Integer>> prefixGroups = new TreeMap<>();
        List<String> plainNames = new ArrayList<>();
        for (String name : names) {
            Matcher m = NUMBERED.matcher(name);
            if (m.matches()) {
                String prefix = m.group(1);
                int num = Integer.parseInt(m.group(2));
                prefixGroups.computeIfAbsent(prefix, k -> new ArrayList<>()).add(num);
            } else {
                plainNames.add(name);
            }
        }
        Collections.sort(plainNames);

        JsonArray array = new JsonArray();
        for (Map.Entry<String, List<Integer>> entry : prefixGroups.entrySet()) {
            List<Integer> nums = entry.getValue();
            Collections.sort(nums);
            int min = nums.get(0);
            int max = nums.get(nums.size() - 1);
            boolean isConsecutive = nums.size() == (max - min + 1);
            if (isConsecutive) {
                JsonObject range = new JsonObject();
                range.addProperty("prefix", entry.getKey());
                range.addProperty("from", min);
                range.addProperty("to", max);
                array.add(range);
            } else {
                for (int n : nums) {
                    array.add(entry.getKey() + " (" + n + ")");
                }
            }
        }
        for (String name : plainNames) {
            array.add(name);
        }
        return array;
    }

    private static JsonObject serializeEntityTypeRule(EntityTypeRule rule) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "entity_type");
        obj.addProperty("entityType", rule.entityType);
        return obj;
    }

    private static JsonObject serializeTextDisplayRule(TextDisplayRule rule) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "text_display");
        obj.addProperty("regex", rule.pattern.pattern());
        if (rule.ownerFilter != null && rule.ownerFilter != TextDisplayRule.OwnerFilter.ALL) {
            obj.addProperty("ownerFilter", rule.ownerFilter.name());
        }
        return obj;
    }

    private static JsonObject serializeArmorStandRule(ArmorStandRule rule) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "armor_stand");
        JsonArray models = new JsonArray();
        rule.models.stream().map(Enum::name).sorted().forEach(models::add);
        obj.add("models", models);
        return obj;
    }

    private static JsonObject serializeItemEntityRule(ItemEntityRule rule) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "item_entity");
        String itemId = BuiltInRegistries.ITEM.getKey(rule.item).toString();
        obj.addProperty("item", itemId);
        return obj;
    }
}
