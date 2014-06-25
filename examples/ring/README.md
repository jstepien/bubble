# Ring in a Bubble

Start the app with

    lein run

and reload the code in the bubble with the lein-bubble plugin:

    lein bubble blow localhost 9090 bubble-ring.main/*bubble* \
        :after 'bubble-ring.core/stop!' \
        :before 'bubble-ring.core/start!' \
        src/bubble_ring/log.clj src/bubble_ring/core.clj
