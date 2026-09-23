package net.cinderlabsmc.cinderlib.example;

import java.nio.file.Path;
import java.util.Map;

import net.cinderlabsmc.cinderlib.datagen.CinderDataGen;

/**
 * Generates the example assets and data. Run {@link #main} with the output
 * directory as first argument, default
 * {@code common/src/generated/resources}.
 */
public final class ExampleDataGen {

  private ExampleDataGen() {
  }

  public static void main(String[] args) {
    ExampleMod.define();

    CinderDataGen.create(ExampleMod.MOD_ID)
        .blocks(ExampleMod.REGISTRAR)
        .item("ruby")
        .lang("itemGroup.cinderexample.main", "CinderLib Example")
        .shapeless("ruby_from_marble", "cinderexample:ruby", 2, "cinderexample:marble")
        .shaped("marble_from_ruby", "cinderexample:marble", 1, new String[] { "RR", "RR" },
            Map.of('R', "cinderexample:ruby"))
        .itemTag("gems", "cinderexample:ruby")
        .apply(ExampleWorldgen::generate)
        .run(Path.of(args.length > 0 ? args[0] : "common/src/generated/resources"));
  }
}
