ReIC integrated circuits
========================

ReIC is a CraftBook-inspired version of integrated circuits. As of writing,
it depends on sk89q's framework for his server and is yet to be ported to be
standalone; however, ReIC has the following features:

* Built in index of integrated circuits
* Built-in documentation
* Push-based approach to dispatching messages and events
* IC debugging information available for ICs
* Smart IC suggestions when misspelling an IC name
* IC names rather than numbers now
* A new more consistent (with Minecraft) way of detecting signal input

ReIC runs side-by-side CraftBook without error.

ReIC was written in a spurt of a few hours and therefore has VERY LITTLE
source documentation.

Compiling
---------

You need to have Maven installed (http://maven.apache.org). Once installed,
simply run:

    mvn clean package install
    
Maven will automatically download dependencies for you. Note: For that to work,
be sure to add Maven to your "PATH".

Contributing
------------

We happily accept contributions. The best way to do this is to fork ReIC
on GitHub, add your changes, and then submit a pull request. We'll look at it,
make comments, and merge it into ReIC if everything works out.

By submitting code, you agree to place to license your code under the 
irrevocable GNU Lesser General Public License v3.