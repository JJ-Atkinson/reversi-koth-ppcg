(defproject reversi-koth "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :repositories {"jitpack github repos" "https://jitpack.io"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha10"]
                 [org.clojure/tools.namespace "0.2.11"]
                 [net.mikera/core.matrix "0.60.3"]
                 [com.github.nathanmerrill/KoTHComm "1.0.10"]]

  :source-paths ["src" "src/main/clojure"]
  :java-source-paths ["src/main/java"]

  ;:main ^:skip-aot gorilla-test.core
  :target-path "target/%s"
  :plugins [[lein-gorilla "0.4.0"]
            [lein-autoreload "0.1.1"]])
