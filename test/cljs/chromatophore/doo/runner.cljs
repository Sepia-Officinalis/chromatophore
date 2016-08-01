(ns chromatophore.doo.runner
  (:require [chromatophore.utilities-test]
            [chromatophore.devcards.markdown]
            [doo.runner :refer-macros [doo-tests]]))

(doo-tests 'chromatophore.utilities-test 'chromatophore.devcards.markdown)
