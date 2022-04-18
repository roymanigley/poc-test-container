# POC TestContainers in dev mode

> the goal is to have test containers used in dev mode with a persistent volume


1. disable `testcontainers/ryuk`
> `ryuk` is a container for cleanup

   export TESTCONTAINERS_RYUK_DISABLED=true

2. define the 