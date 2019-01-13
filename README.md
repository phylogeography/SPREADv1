
**Important** 
This software is no longer actively maintained, users are encouraged to migrate to [SpreaD3](https://github.com/phylogeography/SpreaD3).

# .:: SPREAD ::.
*Spatial Phylogenetic Reconstruction of Evolutionary Dynamics*
Version: 1.0.7, 2015
Authors: Filip Bielejec, Andrew Rambaut, Marc A. Suchard & Philippe Lemey
Homepage: www.phylogeography.org
License: LGPL

### LICENSE

  This is free software; you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as
  published by the Free Software Foundation; either version 2
  of the License, or (at your option) any later version.
 
   This software is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   "GNU Lesser General Public License":http://www.gnu.org/licenses/lgpl.html for more details.
 
### PURPOSE
                            
SPREAD is a Java program supporting processing and visualizations of BEAST's phytogeographic models ("BEAST software":http://beast.bio.ed.ac.uk/Main_Page). 

Contains visualisations (templates), wrapped in a user friendly interface. Supported visualisations include embedded Processing applets and KML output for viewing in "Google Earth":http://www.google.com/earth or any other virtual globe software capable of reading the format.

A tutorial on using SPREAD is availiable "here":http://www.kuleuven.be/aidslab/phylogeography/tutorial/spread_tutorial.html

### SUPPORT & LINKS

SPREAD: www.phylogeography.org/SPREAD
PROCESSING libraries: http://processing.org/ 

### COMPILING

Depends on the following libraries:

* jebl.jar
* core.jar
* colt.jar
* quaqua.jar (libquaqua64.jnilib, libquaqua.jnilib)

### CONTRIBUTING

You're interested in contributing to SPREAD? *THAT'S AWESOME!* Here are the basic steps:

1. fork SPREAD from here: http://github.com/phylogeography/SPREAD
2. Hack, hack, hack!
3. Document your changes in the Changelog
5. Push the changes
6. Send a pull request to the phylogeography/SPREAD project.

### TODO

* Left panel stretches if a parsed attribute name is long
* (Continous Tree) Relate the HPD areas shown as polygons in GoogleEarth with specific nodes of the tree
* Rewrite Log file reader 
* Mean tree calculation (from .trees file)
* Combobox for relaxed (from parser) / standard brownian motion (rate of 1)
* Annotate the terminals in the tree with corresponding taxon names
* Move all the parsing code to reader classes for trees -> avoid code duplication  
* Add command line interface for kml templates 
* Fix large scaling factor issues
