# Smartphone Experimentation

This repository contains all components required to interact with the Organicity Smartphone Experimentation Service.
In more detail:

1. `web-service-web` contains the service that orchestrates the experimentation on the smartphones and contains all information about active experiments and their configuration.
1. `common` contains a mapping of the data transfer objects of the API of `web-servcice-web`.
1. `client-android` is a client for the aforementioned API for android.
1. `client` is a pure java client for the same API.

## Setting up the Development Environment

Smartphone Experimentation is based on the Ambient Dynamix project and the execution of OSGi plugins inside a
host Android application. Specific security and privacy checks are performed internally to restrict the usage
to sensors and interfaces on the phones based on the user's preferences. Information about how to install the
Ambient Dynamix development environment in is available on the Ambient Dynamix [Website](http://ambientdynamix.org/documentation/plug-in-development-guide).


In the context of Smartphone Experimentation there are two types of OSGi plugins to be used:

* Sensors and
* Experiments

Both operate on an async call for a context update and provide data back to the SET wrapper for this request. (calls
to handleContextRequest )

Experiment Plugins can access data from all Sensor plugins and :

* either simply aggregate them to a single measurement to upload to the server or
* do internal calculations and format the results as the message to upload to the server.
* 
Code templates and examples for sensor and experiment plugins are available in the following repositories:

* [Sensor Plugins](https://github.com/OrganicityEu/smartphone-experimentation-plugins)
* [Experiment Plugins](https://github.com/OrganicityEu/smartphone-experimentation-experiments)

All of them are available to use without the need for any development. If experimenters need to use any of them,
they simply need to select them during the creation of the experiment.

## Experimenter Portal

The Smartphone Experimentation cloud interface is tightly integrated with the OrganiCity Experimenter's portal.
Most of the functionalities for managing the experimentation flow. In order to configure a new Experiment
Experimenters need to follow the steps presented bellow:

* Create an Account for OrganiCity and be accepted as an Experimenter.
* Register a new Experiment and fill in all the required information.
* Draw a number of regions of interest on the Map for the data collection to take place.
* Create an Application of type "Smartphone Application".
* Now your application is ready to be executed! (not realy :P)

There are <i>three operations that differentiate</i> the creation of a Smartphone Crowdsensing Experiment from
the rest
of the OrganiCity Experiments.

First, when the experimentation areas are defined, the experimenter need to specify more information including:

1. the expected <b>number of samples</b> (minimum and expected) for collection by the experimenters,
1. the <b>time spans</b> for the experimentation to be executed,
1. the <b>importance</b> of the area in the whole experiment.

Secondly, the experimenter needs to select which sensors the experiment will use on the smartphone.
The experimenter can select any of the available sensor plugins without the need for uploading any piece of
code.
In the case that the experimenter needs to upload a new sensor plugin, the upload can be done from this link.
Any new sensor plugin can be either public or private based on the choice of the experimenter.

Finally, the experimenter needs to provide the jar file that contains the business logic of the experiment.
This is a specific jar file for the experiment and is responsible for preparing the data for upload to the
Smartphone Experimentation Storage Services.

<!--## Monitoring the progress of an experiment-->

<!--To monitor the progress of a Smartphone Crowdsourcing experiment users need to visit the [Smartphone Experimentation portal](https://set.organicity.eu) and select his experiment from the given list.-->

<!--## Retrieving results from an experiment-->

<!--Data aquired from the experiment are always available for download from the [Smartphone Experimentation portal](https://set.organicity.eu) via the appropriate links provided in the Experiments's status view. Currenlty experiments can be downloaded as JSON and CSV dumps. More options can be made avaialable in the future based on requirements.-->
