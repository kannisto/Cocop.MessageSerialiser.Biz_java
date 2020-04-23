Generation of XML proxy classes
===============================

Author: Petri Kannisto, Tampere University, Finland  
Last modified: 4/2020


Assumptions in this file
------------------------

It is assumed that you run Java in Windows. However, it is likely that the
XJC tool like works similarly in all environments.

Another assumption is that "xjc.exe" is located in path
"C:\Program Files\Java\jdk8\bin\xjc.exe". Change the path in each command if
your system has the tool in another location.


Steps
-----

1. Choose a working directory other than this application to prevent any
   accidental overwrites. This is later referred to as "(workdir)".

2. Retrieve B2MML schemata (B2MML-BatchML V0600) from
   [http://www.mesa.org/en/B2MML.asp](http://www.mesa.org/en/B2MML.asp)

3. Add the required extension(s) to B2MML schemata. These are explained in
   file "Schemata/cocop_extension.xsd".

4. Copy B2MML schemata to (workdir)/b2mml

5. Copy "bindings.xml" to (workdir)

6. In (workdir), run the following command.

```
"C:\Program Files\Java\jdk8\bin\xjc.exe" -b bindings.xml b2mml\B2MML-V0600-ProductionSchedule.xsd
```

7. Copy the generated folder ("org") to "MessagesJaxb/src" (in this project). That
   is, you will have the following folder structure.

```
MessagesJaxb/src/org/...
```
