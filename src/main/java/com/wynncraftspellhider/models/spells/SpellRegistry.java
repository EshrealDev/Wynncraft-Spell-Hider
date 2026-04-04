package com.wynncraftspellhider.models.spells;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SpellRegistry {

    public enum WynnClass {
        WARRIOR, MAGE, ARCHER, ASSASSIN, SHAMAN, GLOBAL
    }

    private static final List<SpellConfig> WARRIOR_SPELLS = new ArrayList<>();
    private static final List<SpellConfig> MAGE_SPELLS = new ArrayList<>();
    private static final List<SpellConfig> ARCHER_SPELLS = new ArrayList<>();
    private static final List<SpellConfig> ASSASSIN_SPELLS = new ArrayList<>();
    private static final List<SpellConfig> SHAMAN_SPELLS = new ArrayList<>();
    private static final List<SpellConfig> GLOBAL_SPELLS = new ArrayList<>();

    // Generates Set.of("prefix (1)", "prefix (2)", ..., "prefix (n)")
    private static Set<String> range(String prefix, int from, int to) {
        return IntStream.rangeClosed(from, to)
                .mapToObj(i -> prefix + " (" + i + ")")
                .collect(Collectors.toUnmodifiableSet());
    }

    static {
        // WARRIOR
        WARRIOR_SPELLS.add(new SpellConfig("Melee", List.of(
                new SpellGroup("Melee", MatchRule.of(range("warrior_meleeswipe", 1, 5)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Bash", List.of(
                new SpellGroup("Bash", MatchRule.of(range("bash", 1, 7)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Uppercut", List.of(
                new SpellGroup("Uppercut", MatchRule.of(range("uppercut", 1, 7)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("War Scream", List.of(
                new SpellGroup("War scream", MatchRule.of(range("warscream", 1, 9)))
        )));


        //Paladin
        WARRIOR_SPELLS.add(new SpellConfig("Flaming Uppercut", List.of(
                new SpellGroup("Flaming uppercut", MatchRule.of(range("flaminguppercut", 1, 8)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Mantle of the Bovemists", List.of(
                new SpellGroup("Mantle", MatchRule.of("mantle")),
                new SpellGroup("Mantle break", MatchRule.of(range("mantlebreak", 1, 9)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Shield Strike", List.of(
                new SpellGroup("Shield strike mantle", MatchRule.of("shieldstrikemantle")),
                new SpellGroup("Mantle attack", MatchRule.of(range("shieldstrike", 1, 13)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Sparkling Hope", List.of(
                new SpellGroup("Sparkling hope", MatchRule.of(range("sparklinghope", 1, 5)))
        )));

        //todo: add more customizability to the spell via hashes.
        WARRIOR_SPELLS.add(new SpellConfig("Sacred Surge", List.of(
                new SpellGroup("Sacred surge", MatchRule.of("sacredsurge"))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Emboldening Cry", List.of(
                new SpellGroup("Emboldening cry", MatchRule.of(range("emboldeningcry", 1, 12)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Radiance", List.of(
                new SpellGroup("radiance", MatchRule.of(range("radiance", 1, 13)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Heavenly Trumpet", List.of(
                new SpellGroup("Heavenly trumpet", MatchRule.of("heavenlytrumpet")),
                new SpellGroup("Heavenly trumpet circle", MatchRule.of(range("heavenlytrumpetcircle", 1, 15)))
        )));

        //todo: check if everything is actually hidden. takes time to test.
        WARRIOR_SPELLS.add(new SpellConfig("Second Chance", List.of(
                new SpellGroup("Second chance", MatchRule.of(range("secondchance", 1, 9)))
        )));

        //todo: check if this interferes with the beams in NOL.
        WARRIOR_SPELLS.add(new SpellConfig("Buried Light",
                "This setting might interfere with the beams in NOL.\n\nPlease let Eshreal know if it does.",
                List.of(
                new SpellGroup("Buried light", MatchRule.of("buriedlight"))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Judgement",
                "To adjust the white border circle please go to the global tab.",
                List.of(
                new SpellGroup("Judgement sword", MatchRule.of("judgementsword")),
                new SpellGroup("Judgement hit circle", MatchRule.of(range("judgementhitcircle", 1, 2))),
                new SpellGroup("Judgement hit circle white", MatchRule.of("judgementwhitecircle", Set.of("0d547f7ab5d93aab99699369054910576bab7388ecfa6c69e56a57dd6be88ac1")))
        )));


        //Battle monk
        WARRIOR_SPELLS.add(new SpellConfig("Half-Moon Swipe", List.of(
                new SpellGroup("Half-moon swipe", MatchRule.of(range("halfmoonswipe", 1, 6)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Air shout", List.of(
                new SpellGroup("Air shout", MatchRule.of(range("airshout", 1, 18)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Flying Kick", List.of(
                new SpellGroup("Flying Kick", MatchRule.of(range("flyingkick", 1, 8)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Whirlwind Strike", List.of(
                new SpellGroup("Whirlwind strike", MatchRule.of(range("whirlwindstrike", 1, 10)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Tempest", List.of(
                new SpellGroup("Tempest", MatchRule.of(range("tempest", 1, 4)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Cyclone", List.of(
                new SpellGroup("Cyclone", MatchRule.of("warriorcyclone"))
        )));

        //todo: fix: both textures reference the same model. it is 1 model. so there is an error in the console.
        WARRIOR_SPELLS.add(new SpellConfig("Thunderclap", List.of(
                new SpellGroup("Thunderclap", MatchRule.of(range("warriorthunderclap", 1, 2)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Zenith Stance", List.of(
                new SpellGroup("Zenith stance", MatchRule.of("zenithstance"))
        )));


        // Fallen
        WARRIOR_SPELLS.add(new SpellConfig("Fireworks", List.of(
                new SpellGroup("Fireworks", MatchRule.of(range("warriorfireworks", 1, 7)))
        )));

        //todo: maybe add more configuration to the jaw of the skull and circle.
        WARRIOR_SPELLS.add(new SpellConfig("Bak'al's Grasp", List.of(
                new SpellGroup("Bak'al's grasp", MatchRule.of(range("bakalsgrasp", 1, 2)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Boiling Blood", List.of(
                new SpellGroup("Boiling blood", MatchRule.of("warriorboilingblood"))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Bloodlust", List.of(
                new SpellGroup("Bloodlust", MatchRule.of(range("warriorbloodlust", 1, 9)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Bloodied Armory", List.of(
                new SpellGroup("Bloodied armory", MatchRule.of(range("bloodiedarmory", 1, 6)))
        )));

        WARRIOR_SPELLS.add(new SpellConfig("Beyond Salvation", List.of(
                new SpellGroup("Beyond salvation", MatchRule.of(range("beyondsalvation", 1, 12)))
        )));









        // MAGE
        MAGE_SPELLS.add(new SpellConfig("Melee", List.of(
                new SpellGroup("Melee", MatchRule.of("meleebeam"))
        )));

        MAGE_SPELLS.add(new SpellConfig("Meteor",
                "Meteor explosion and archer traps explosion use identical models. Editing this will modify traps too.",
                List.of(
                new SpellGroup("Meteor trail", MatchRule.of("meteor", Set.of("4aa761cc79b8e1a17d786253ef423aca6bb92cd65f5afba467a6c38d36662702", "5559c862acd173f8f333eb409634cd6f5b7582e53a15013b98cb19348ea3c123", "fecc509fe5d212d14d9cf3bf93b339c80076f33cd7cc8254b0a93b8beeef0927"))),
                new SpellGroup("Meteor ball",MatchRule.of("meteor", Set.of("4918908e2c891617ee71ca135251d6e17c610e40e7aa47670fd449b9a8dd3518"))),
                new SpellGroup("Meteor circle", MatchRule.of(range("meteorcircle", 1, 11))),
                new SpellGroup("Meteor explosion", MatchRule.of(range("meteorexplosion", 1, 16))),
                new SpellGroup("Meteor skycircle", MatchRule.of(range("meteorskycircle", 1, 6))),
                new SpellGroup("Meteor beam", MatchRule.of("meteorbeam"))
        )));

        MAGE_SPELLS.add(new SpellConfig("Heal", List.of(
                new SpellGroup("Heal circle", MatchRule.of(range("heal", 1, 7)))
        )));

        MAGE_SPELLS.add(new SpellConfig("Ice snake", List.of(
                new SpellGroup("Ice snake", MatchRule.of("icesnake"))
        )));

        MAGE_SPELLS.add(new SpellConfig("Teleport", List.of(
                new SpellGroup("Teleport beam", MatchRule.of("teleport"))
        )));


        //Arcanist
        MAGE_SPELLS.add(new SpellConfig("Burning Sigil", List.of(
                new SpellGroup("Burning sigil", MatchRule.of(range("burningsigil", 1, 11))),
                new SpellGroup("Burning sigil text", MatchRule.of(range("burningsigiltext", 1, 11)))
        )));

        MAGE_SPELLS.add(new SpellConfig("Arcane Transfer", List.of(
                new SpellGroup("Arcane transfer", MatchRule.of(range("arcanetransfer", 1, 13)))
        )));

        MAGE_SPELLS.add(new SpellConfig("Pyrokinesis", List.of(
                new SpellGroup("Pyrokinesis", MatchRule.of(range("pyrokinesis", 1, 9)))
        )));

        MAGE_SPELLS.add(new SpellConfig("Judrajim", List.of(
                new SpellGroup("Judrajim", MatchRule.of("judrajim"))
        )));

        MAGE_SPELLS.add(new SpellConfig("Thunderstorm",
                "Adjusting this will also effect the thunderbolts from Angelic Ascension (boltslinger ultimate) and from Sundered Skies (ritualist ultimate).\n\nThey use the same textures.",
                List.of(
                new SpellGroup("Thunderstorm", MatchRule.of(range("thunderstorm", 1, 10)))
        )));

        MAGE_SPELLS.add(new SpellConfig("Twisted Origin", List.of(
                new SpellGroup("Red dragon", MatchRule.of("reddragon")),
                new SpellGroup("Yellow dragon", MatchRule.of("yellowdragon")),
                new SpellGroup("Yellow dragon beam", MatchRule.of(range("yellowdragonbeam", 1, 41)))
        )));




        //Riftwalker
        MAGE_SPELLS.add(new SpellConfig("Astral Fragmentation", List.of(
                new SpellGroup("Astral fragmentation", MatchRule.of("astralfragmentation"))
        )));

        MAGE_SPELLS.add(new SpellConfig("Time Vortex", List.of(
                new SpellGroup("Time vortex", MatchRule.of("timevortex"))
        )));

        MAGE_SPELLS.add(new SpellConfig("Vacuokinesis", List.of(
                new SpellGroup("Vacuokinesis", MatchRule.of(range("vacuokinesis", 1, 5)))
        )));

        //todo: check if purplefireball interferes with the fireballs in NOL.
        MAGE_SPELLS.add(new SpellConfig("Dimensional Tear", List.of(
                new SpellGroup("Dimensional tear", MatchRule.of(range("dimensionaltear", 1, 25))),
                new SpellGroup("Fireball", MatchRule.of("purplefireball"))
        )));

        

        //Lightbender
        MAGE_SPELLS.add(new SpellConfig("Sunshower", List.of(
                new SpellGroup("Sunshower", MatchRule.of(range("sunshower", 1, 9)))
        )));

        MAGE_SPELLS.add(new SpellConfig("Ophanim & Lightweaver", List.of(
                new SpellGroup("Ophanim & Lightweaver balls", MatchRule.of(range("ophanimballs", 1, 10))),
                new SpellGroup("Ophanim back", MatchRule.of("ophanimback"))
        )));

        MAGE_SPELLS.add(new SpellConfig("Freezing Sigil", List.of(
                new SpellGroup("Freezing sigil", MatchRule.of(range("freezingsigil", 1, 13)))
        )));

        MAGE_SPELLS.add(new SpellConfig("Sunflare", List.of(
                new SpellGroup("Sunflare", MatchRule.of(range("sunflare", 1, 12)))
        )));

        //todo: I do not know if interweave has a model. I would have to test with another player
        //MAGE_SPELLS.add(new SpellConfig("Interweave", List.of(
        //         new SpellGroup("Interweave", MatchRule.of(range("Interweave", 1, 12)))
        //)));

        //todo: check if this interferes with the sun in NOL.
        MAGE_SPELLS.add(new SpellConfig("Dawn",
                "This setting might interfere with NOL.\n\nPlease let Eshreal know if it does.",
                List.of(
                new SpellGroup("Sun", MatchRule.of("sun")),
                new SpellGroup("Fireball", MatchRule.of("fireball"))
        )));









        // ARCHER
        ARCHER_SPELLS.add(new SpellConfig("Arrows",
                "Affects all arrows rendered in the world that are arrow entities.\n\nThis includes arrows fired by the player, other players, and mobs.",
                List.of(
                new SpellGroup("Arrows", MatchRule.ofEntityType("arrow"))
        )));

        ARCHER_SPELLS.add(new SpellConfig("Arrow Bomb",
                "Arrow bomb explosion will also effect exploding puppets.\n\nThey use the same textures.",
                List.of(
                new SpellGroup("Arrow bomb explosion", MatchRule.of(range("arrowbombexplosion", 1, 8)))
        )));

        ARCHER_SPELLS.add(new SpellConfig("Escape", List.of(
                new SpellGroup("Escape", MatchRule.of(range("archerescape", 1, 5)))
        )));

        ARCHER_SPELLS.add(new SpellConfig("Arrow Shield", List.of(
                new SpellGroup("Arrow shield", MatchRule.of("arrowshield"))
        )));

        ARCHER_SPELLS.add(new SpellConfig("Arrow Storm", List.of(
                new SpellGroup("Arrow storm circle", MatchRule.of(range("arrowstormcircle", 1, 13)))
        )));

        //Sharpshooter
        ARCHER_SPELLS.add(new SpellConfig("Phantom Ray", List.of(
                new SpellGroup("Phantom ray", MatchRule.of("phantomray"))
        )));

        ARCHER_SPELLS.add(new SpellConfig("Twain's Arc", List.of(
                new SpellGroup("Twain's arc bow", MatchRule.of("twainsarcbow")),
                new SpellGroup("Twain's arc shot", MatchRule.of(range("twainsarcshot", 1, 11)))
        )));

        ARCHER_SPELLS.add(new SpellConfig("Crepuscular Ray", List.of(
                new SpellGroup("Crepuscular ray", MatchRule.of(range("crepuscularray", 1, 6)))
        )));

        ARCHER_SPELLS.add(new SpellConfig("Repel", List.of(
                new SpellGroup("Repel", MatchRule.of(range("archerrepel", 1, 6)))
        )));

        ARCHER_SPELLS.add(new SpellConfig("The Twilight Zone", List.of(
                new SpellGroup("The twilight zone bow", MatchRule.of("thetwilightzonebow")),
                new SpellGroup("The twilight zone shot", MatchRule.of(range("thetwilightzoneshot", 1, 15)))
        )));

        // Trapper
        //todo: add support for traps, crows and snakes. they are most likely armor stands. similar to the knives of assassin.
        ARCHER_SPELLS.add(new SpellConfig("Bryophyte Roots", List.of(
                new SpellGroup("Bryophyte Roots", MatchRule.of("bryophyteroots"))
        )));

        //todo: add name tag support. that is still visible. probably text displays.
        ARCHER_SPELLS.add(new SpellConfig("Call of the Hound", List.of(
                new SpellGroup("Call of the hound", MatchRule.of("callofthehound"))
        )));

        ARCHER_SPELLS.add(new SpellConfig("Chilling Snare", List.of(
                new SpellGroup("Chilling Snare", MatchRule.of("chillingsnare"))
        )));

        ARCHER_SPELLS.add(new SpellConfig("Extinction Event", List.of(
                new SpellGroup("Extinction event bomb", MatchRule.of("extinctioneventbomb")),
                new SpellGroup("Extinction event bomb fire", MatchRule.of(range("extinctioneventbombfire", 1, 3))),
                new SpellGroup("Extinction event crow", MatchRule.of("extinctioneventcrow"))
        )));

        //Boltslinger
        ARCHER_SPELLS.add(new SpellConfig("Guardian Angels", List.of(
                new SpellGroup("Guardian angels", MatchRule.of("guardianangels"))
        )));

        ARCHER_SPELLS.add(new SpellConfig("Fierce Stomp", List.of(
                new SpellGroup("Fierce stomp", MatchRule.of(range("fiercestomp", 1, 9)))
        )));

        //Judgement uses the exact same texture as this. It's the fully white circles. We use model hashes.
        ARCHER_SPELLS.add(new SpellConfig("Courier's Edict", List.of(
                new SpellGroup("Courier's edict", MatchRule.of("judgementwhitecircle", Set.of("2bafb54591222834561f8d43b5a60b1959e76e2a0e3c4866333c1b7b1677041c")))
        )));

        //I tried looking into the hashes. but the hashes are also the same. they do have different modelids and 1 is always 2 lower than thunderstorm.
        ARCHER_SPELLS.add(new SpellConfig("Angelic Ascension",
                "To adjust the thunderstorm from the arrow bomb please adjust the mage ability.\n\nThey use they same textures.",
                List.of(
                        new SpellGroup("Angelic ascension guardian angel", MatchRule.of("angelicascensionguardianangel")),
                        new SpellGroup("Angelic ascension wings", MatchRule.of(range("angelicascensionwings", 1, 2)))
        )));








        // ASSASSIN
        ASSASSIN_SPELLS.add(new SpellConfig("Melee", List.of(
                new SpellGroup("Melee", MatchRule.of(range("assassinmelee", 1, 8)))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Spin Attack", List.of(
                new SpellGroup("Spin attack", MatchRule.of(range("spinattack", 1, 7)))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Multi Hit", List.of(
                new SpellGroup("Multi hit swipe", MatchRule.of(range("multihitswipe", 1, 6))),
                new SpellGroup("Multi hit enemy", MatchRule.of(range("multihitenemy", 1, 2)))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Smoke Bomb", List.of(
                new SpellGroup("Smoke bomb", MatchRule.of("smokebomb"))
        )));

        // Acrobat
        ASSASSIN_SPELLS.add(new SpellConfig("Ripple", List.of(
                new SpellGroup("Ripple", MatchRule.of(range("assassinripple", 1, 14)))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Lacerate", List.of(
                new SpellGroup("Lacerate", MatchRule.of(range("lacerate", 1, 8)))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Shurikens", List.of(
                new SpellGroup("Shurikens", MatchRule.of("shurikens"))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Jasmine Bloom", List.of(
                new SpellGroup("Jasmine bloom", MatchRule.of(range("jasminebloom", 1, 7)))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Serpent's Garden", List.of(
                new SpellGroup("Serpent's garden flowers", MatchRule.of(range("serpentsgardenflowers", 1, 4))),
                new SpellGroup("Serpent's garden leap effect", MatchRule.of(range("serpentsgardenleapeffect", 1, 5)))
        )));

        // Trickster
        //todo: maybe add support for clones. they appear to be Type: entity.minecraft.player. but not sure. looked for 2 seconds.
        ASSASSIN_SPELLS.add(new SpellConfig("Bamboozle", List.of(
                new SpellGroup("Bamboozle", MatchRule.of(range("bamboozle", 1, 8)))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Vanish", List.of(
                new SpellGroup("Vanish", MatchRule.of(range("vanish", 1, 10)))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Malicious Mockery", List.of(
                new SpellGroup("Malicious mockery", MatchRule.of(range("maliciousmockery", 1, 10)))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Misdirection", List.of(
                new SpellGroup("Misdirection", MatchRule.of("confusedstar"))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Toxic Sludge", List.of(
                new SpellGroup("Toxic sludge", MatchRule.of(range("toxicsludge", 1, 16)))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Deflagrate", List.of(
                new SpellGroup("Deflagrate", MatchRule.of("deflagrate"))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Noxious Haze", List.of(
                new SpellGroup("Noxious haze smoke bomb", MatchRule.of("noxioushazesmokebomb"))
        )));

        // Shadestepper
        ASSASSIN_SPELLS.add(new SpellConfig("Backstab", List.of(
                new SpellGroup("Backstab", MatchRule.of(range("backstab", 1, 4)))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Surprise Strike", List.of(
                new SpellGroup("Surprise strike", MatchRule.of(range("surprisestrike", 1, 6)))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Black Hole", List.of(
                new SpellGroup("Black hole smoke bomb", MatchRule.of(range("blackholesmokebomb", 1, 9)))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Violent Vortex", List.of(
                new SpellGroup("Violent vortex", MatchRule.of(range("violentvortex", 1, 10)))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Marked", List.of(
                new SpellGroup("Marked", MatchRule.of("assassinmarked"))
        )));

        //todo: add support for the knives at the back of the player. they have custom model data but it's inside an armor stand.
        ASSASSIN_SPELLS.add(new SpellConfig("Nightcloak Knives", List.of(
                new SpellGroup("Nightcloak knives", MatchRule.of(range("nightcloakknivesattack", 1, 7)))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Satsujin", List.of(
                new SpellGroup("Satsujin", MatchRule.of(range("satsujin", 1, 10)))
        )));

        ASSASSIN_SPELLS.add(new SpellConfig("Pierce the Veil", List.of(
                new SpellGroup("Pierce the veil", MatchRule.of("piercetheveil"))
        )));





        // SHAMAN
        SHAMAN_SPELLS.add(new SpellConfig("Melee", List.of(
                new SpellGroup("Melee", MatchRule.of(range("shamanmelee", 1, 4)))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Totem", List.of(
                new SpellGroup("Totem", MatchRule.of(range("shamantotem", 1, 2))),
                new SpellGroup("Totem circle", MatchRule.of("shamantotemcircle")),
                new SpellGroup("Totem effects", MatchRule.of(range("shamantotemeffects", 1, 4)))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Totemic Smash", List.of(
                new SpellGroup("Totemic smash", MatchRule.of(range("totemicsmash", 1, 13)))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Aura", List.of(
                new SpellGroup("Aura", MatchRule.of("shamanaura"))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Uproot", List.of(
                new SpellGroup("Uproot", MatchRule.of(range("shamanuproot", 1, 5)))
        )));

        //Acolyte
        SHAMAN_SPELLS.add(new SpellConfig("Sacrificial Shrine", List.of(
                new SpellGroup("Sacrificial shrine aura", MatchRule.of("sacrificialshrineaura"))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Eldritch Call", List.of(
                new SpellGroup("Eldritch call tentacle", MatchRule.of("eldritchcalltentacle")),
                new SpellGroup("Eldritch call tentacle hit", MatchRule.of(range("eldritchcalltentaclehit", 1, 4)))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Blood Sorrow", List.of(
                new SpellGroup("Blood sorrow square", MatchRule.of(range("bloodsorrowsquare", 1, 8))),
                new SpellGroup("Blood sorrow effects", MatchRule.of(range("bloodsorroweffects", 1, 21)))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Moment to Gloom", List.of(
                new SpellGroup("Moment to gloom totem", MatchRule.of(range("monumenttogloomtotem", 1, 2)))
        )));

        //Ritualist
        SHAMAN_SPELLS.add(new SpellConfig("Rain Dance", List.of(
                new SpellGroup("Rain dance cloud", MatchRule.of("raindancecloud")),
                new SpellGroup("Rain dance rain", MatchRule.of(range("raindancerain", 1, 4)))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Frog Dance", List.of(
                new SpellGroup("Frog dance bounce", MatchRule.of(range("frogdancebounce", 1, 10)))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Totemic Shatter", List.of(
                new SpellGroup("Totemic shatter", MatchRule.of(range("totemicshatter", 1, 9)))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Tribal Chants", List.of(
                new SpellGroup("Tribal chant lunatic", MatchRule.of(range("tribalchantlunatic", 1, 8))),
                new SpellGroup("Tribal chant fanatic", MatchRule.of(range("tribalchantfanatic", 1, 8))),
                new SpellGroup("Tribal chant heretic", MatchRule.of(range("tribalchantheretic", 1, 10)))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Awakened", List.of(
                new SpellGroup("Awakened circle", MatchRule.of("awakenedcircle")),
                new SpellGroup("Awakened effects", MatchRule.of(range("awakenedeffects", 1, 13)))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Pool of Rejuvination", List.of(
                new SpellGroup("Pool of rejuvination circle", MatchRule.of("poolofrejuvinationcircle")),
                new SpellGroup("Pool of rejuvination sparkles", MatchRule.of("poolofrejuvinationsparkles"))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Sundered Skies",
                "If you want to configure the thunderstorm effects. Please configure the mage ability.",
                List.of(
                new SpellGroup("Sundered skies cloud", MatchRule.of("sunderedskiescloud")),
                new SpellGroup("Sundered skies circles", MatchRule.of("sunderedskiescircles"))
        )));

        //Summoner
        SHAMAN_SPELLS.add(new SpellConfig("Puppet Master", List.of(
                new SpellGroup("Puppet", MatchRule.of("puppetmasterpuppet")),
                new SpellGroup("Puppet knife", MatchRule.of("puppetmasterknife"))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Crimson Effigy", List.of(
                new SpellGroup("Crimson effigy", MatchRule.of("crimsoneffigy"))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Commander", List.of(
                new SpellGroup("Commander puppet rally knife", MatchRule.of("commanderpuppetrallyknife"))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Hummingbird's Song", List.of(
                new SpellGroup("Hummingbird carving", MatchRule.of("hummingbirdcarving"))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Invigorating Wave", List.of(
                new SpellGroup("Invigorating wave aura", MatchRule.of("invigoratingwaveaura"))
        )));

        SHAMAN_SPELLS.add(new SpellConfig("Patchwork Abomination", List.of(
                new SpellGroup("Patchwork abomination", MatchRule.of("patchworkabomination"))
        )));





        //GLOBAL
        GLOBAL_SPELLS.add(new SpellConfig("Mob totem & Judgement border",
                "This setting adjusts the mob totem & Judgement border circle.\n\nThey use the same textures.",
                List.of(
                new SpellGroup("Mob totem & Judgement border", MatchRule.of("totembordercircle"))
        )));

        GLOBAL_SPELLS.add(new SpellConfig("Ultimate Charged", List.of(
                new SpellGroup("Ultimate charged", MatchRule.of(range("ultimatecharged", 1, 16)))
        )));

        GLOBAL_SPELLS.add(new SpellConfig("Quake", List.of(
                new SpellGroup("Quake", MatchRule.of(range("quakespecial", 1, 8)))
        )));

        GLOBAL_SPELLS.add(new SpellConfig("Chain lightning", List.of(
                new SpellGroup("Chain lightning", MatchRule.of(range("chainlightningspecial", 1, 5)))
        )));

        GLOBAL_SPELLS.add(new SpellConfig("Curse", List.of(
                new SpellGroup("Curse", MatchRule.of(range("cursespecial", 1, 21)))
        )));

        GLOBAL_SPELLS.add(new SpellConfig("Courage", List.of(
                new SpellGroup("Courage", MatchRule.of(range("couragespecial", 1, 9)))
        )));

        GLOBAL_SPELLS.add(new SpellConfig("Wind Prison", List.of(
                new SpellGroup("Wind prison", MatchRule.of(range("windprisonspecial", 1, 7)))
        )));
    }

    public static List<SpellConfig> getSpells(WynnClass wynnClass) {
        return switch (wynnClass) {
            case WARRIOR -> WARRIOR_SPELLS;
            case MAGE -> MAGE_SPELLS;
            case ARCHER -> ARCHER_SPELLS;
            case ASSASSIN -> ASSASSIN_SPELLS;
            case SHAMAN -> SHAMAN_SPELLS;
            case GLOBAL -> GLOBAL_SPELLS;
        };
    }

    public static List<SpellConfig> getAllSpells() {
        List<SpellConfig> all = new ArrayList<>();
        for (WynnClass wynnClass : WynnClass.values()) {
            all.addAll(getSpells(wynnClass));
        }
        return all;
    }

    public static SpellGroup getGroupByTextureName(String textureName) {
        for (SpellConfig spell : getAllSpells()) {
            for (SpellGroup group : spell.groups) {
                if (group.matchesTexture(textureName)) return group;
            }
        }
        return null;
    }

    public static SpellGroup getGroupForModel(String textureName, String fingerprint) {
        for (SpellConfig spell : getAllSpells()) {
            for (SpellGroup group : spell.groups) {
                if (group.matches(textureName, fingerprint)) return group;
            }
        }
        return null;
    }

    public static SpellGroup getGroupForEntityType(String entityType) {
        for (SpellConfig spell : getAllSpells()) {
            for (SpellGroup group : spell.groups) {
                if (group.isEntityTypeMatch(entityType)) return group;
            }
        }
        return null;
    }
}