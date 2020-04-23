
Cocop.MessageSerialiser.Biz (Java) v2.0.0
=========================================

---

<img src="logos.png" alt="COCOP and EU" style="display:block;margin-right:auto" />

COCOP - Coordinating Optimisation of Complex Industrial Processes  
https://cocop-spire.eu/

This project has received funding from the European Union's Horizon 2020
research and innovation programme under grant agreement No 723661. This piece
of software reflects only the authors' views, and the Commission is not
responsible for any use that may be made of the information contained therein.

---


Author
------

Petri Kannisto, Tampere University, Finland  
https://github.com/kannisto  
http://kannisto.org

**Please make sure to read and understand [LICENSE.txt](./LICENSE.txt)!**


COCOP Toolkit
-------------

This application is a part of COCOP Toolkit, which was developed to enable a 
decoupled and well-scalable architecture in industrial systems. Please see 
https://kannisto.github.io/Cocop-Toolkit/


Introduction
------------

This library provides a generic API to serialise and deserialise (or to encode
and decode) messages that contain production-related schedules. Please
familiarise yourself with this page: 
https://kannisto.github.io/Cocop-Toolkit/messageserialiser.html

All message structures are based on an open standards called Business to
Manufacturing Markup Language (B2MML), which implements the ANSI/ISA-95
standard (also published as IEC/ISO 62264). B2MML is copyrighted to
Manufacturing Enterprise Solutions Association (MESA International;
http://www.mesa.org/en/B2MML.asp ).
However, MESA International is _not_ the creator of this software, and this
software has _not_ been certified for compliance with B2MML.

Because B2MML is complex and versatile, the API selectively implements what was
needed in COCOP project. To guarantee interoperability, the API implements a
_profile_ that covers a subset of B2MML. In addition, there are
extensions not included in B2MML, but these are few.

This software has been implemented with Java. However, there is another 
implementation in C#/.NET (see 
https://github.com/kannisto/Cocop.MessageSerialiser.Biz_csharp). Still, because
the serialisation syntax is XML, the messages do not restrict which platform to
choose for implementation, as long as the messages comply with the
standards-based profile. That is, you could have one application in .NET,
another in Java and third in NodeJs, for instance, and these would be
completely interoperable.

This repository contains the following applications:

* CocopMessageSerialiserBiz: the actual library
* Test*: test applications
* Others: location for XML schemata, etc.


Source Code and API Doc
-----------------------

* Github repo: https://github.com/kannisto/Cocop.MessageSerialiser.Biz_java
* API documentation: https://kannisto.github.io/Cocop.MessageSerialiser.Biz_java


Messages
--------

In the current implementation, only the moduel _ProcessProductionRequest_
generates XML documents. That is, if the intention is to locate any object in a
message, this object must be included in a _ProcessProductionRequest_.


Examples
--------

Due to lack of time, this API does not ship with code examples. However, the
C#/.NET API has an almost similar structure. Its examples can help:
https://kannisto.github.io/Cocop.MessageSerialiser.Biz_csharp

In addition, the enclosed test projects provide hint how to use the API.


Work of Third Parties
---------------------

This software distribution does not include any third-party modules, that is,
the related XML schemata or any proxy software generated from these schemata.
The reason is that the authors are not lawyers but software developers and the
intention is to ensure that any copyright or licensing issues are avoided.
However, because this software library is largely based on B2MML, the related
license conditions apply.

Still, because the third-party modules are necessary for this library to work,
this distribution includes instructions to retrieve them. You must download the
required XML schema files, generate the proxies and modify these as instructed.
For more information, please see the folder _MessagesJaxb_.

Furthermore, all third-party software libraries are excluded.


Environment
-----------

The development environment was _Eclipse Photon Release (4.8.0), Build id:
20180619-1200_. The runtime was JDK8.

The following libraries are needed:

* joda-time-2.9.9.jar
* joda-time-2.9.9-javadoc.jar (if you want Javadoc)

If you want to test the inclusion of scheduling parameters in a production
request, you can use the 'Item\_DataRecord' module from
_Cocop.MessageSerialiser.Meas_. In this case, you must retrieve this
library as well.
