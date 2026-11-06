package com.smokeythebandicoot.witcherycompanion.utils.blood;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Parses the entries of the config option that lists the creatures a Vampire can drink from. Format:
 * <pre>
 *     modid:entity[;amount[;maxFillPercent[;damage]]]
 *     modid:*[;amount[;maxFillPercent[;damage]]]
 * </pre>
 * Missing fields use the values of BloodProfile.DEFAULT. An amount of zero (or less) means that the creature
 * can NOT be drunk from, even if it would be allowed by a lower priority rule. Lines that are empty or start
 * with # are ignored.
 * <p>
 * This class contains no Minecraft types on purpose, so that it can be unit-tested in plain Java.
 */
public final class BloodConfigParser {

    private BloodConfigParser() {}

    /**
     * Fills the ruleSet with the parsed entries
     * @return one human-readable warning for each entry that could not be parsed (and was skipped)
     */
    public static List<String> parse(String[] lines, BloodRuleSet ruleSet) {
        List<String> warnings = new ArrayList<>();
        if (lines == null) return warnings;

        for (String raw : lines) {
            if (raw == null) continue;
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            String[] parts = line.split(";", -1);
            String id = parts[0].trim().toLowerCase(Locale.ROOT);
            if (id.indexOf(':') < 0) id = "minecraft:" + id;

            boolean wildcard = id.endsWith(":*");
            String namespace = wildcard ? id.substring(0, id.length() - 2) : null;
            if (!isValidId(wildcard ? namespace + ":x" : id)) {
                warnings.add("Invalid creature id in '" + line + "'");
                continue;
            }

            BloodProfile profile;
            try {
                profile = parseProfile(parts);
            } catch (NumberFormatException e) {
                warnings.add("Invalid number in '" + line + "'");
                continue;
            }
            if (parts.length > 4) {
                warnings.add("Too many fields in '" + line + "': extra fields ignored");
            }

            if (wildcard) ruleSet.putNamespace(namespace, profile);
            else ruleSet.putId(id, profile);
        }
        return warnings;
    }

    private static BloodProfile parseProfile(String[] parts) {
        int amount = BloodProfile.DEFAULT_AMOUNT;
        int maxFill = BloodProfile.DEFAULT_MAX_FILL_PERCENT;
        float damage = BloodProfile.DEFAULT_DAMAGE;

        if (parts.length > 1 && !parts[1].trim().isEmpty()) amount = Integer.parseInt(parts[1].trim());
        if (amount <= 0) return BloodProfile.BLOCKED;
        if (parts.length > 2 && !parts[2].trim().isEmpty()) maxFill = Integer.parseInt(parts[2].trim());
        if (parts.length > 3 && !parts[3].trim().isEmpty()) damage = Float.parseFloat(parts[3].trim());

        // Values out of range are clamped by the BloodProfile constructor. A max fill of 0 would make the
        // creature useless, so it is raised to 1%
        return new BloodProfile(amount, Math.max(1, maxFill), damage);
    }

    private static boolean isValidId(String id) {
        int colon = id.indexOf(':');
        if (colon <= 0 || colon == id.length() - 1) return false;
        for (int i = 0; i < id.length(); i++) {
            char c = id.charAt(i);
            boolean ok = (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_' || c == '-' || c == '.' ||
                    c == ':' || c == '/';
            if (!ok) return false;
        }
        return id.indexOf(':', colon + 1) < 0;
    }

}
