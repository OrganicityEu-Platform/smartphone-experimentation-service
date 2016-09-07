# Smartphone Experimentation

This repository contains all components required to interact with the Organicity Smartphone Experimentation Service.
In more detail:
1. `web-service-web` contains the service that orchestrates the experimentation on the smartphones and contains all information about active experiments and their configuration.
2. `common` contains a mapping of the data transfer objects of the API of `web-servcice-web`.
3. `client-android` is a client for the aforementioned API for android.
4. `client` is a pure java client for the same API.

## Setting up the Development Environment

tbd

## Coding a new Experiment

For more information about coding the experiments check the following repo:

[Experiments Repository](https://github.com/OrganicityEu/smartphone-experimentation-experiments)

## Coding a new Sensor

For more information about coding the sensors check the following repo:

[Plugins Repository](https://github.com/OrganicityEu/smartphone-experimentation-plugins)

## Testing the Experiment or Sensors during development 

tbd

## Registering a new Experiment with Organicity

tbd

## Monitoring the progress of an experiment

To monitor the progress of a Smartphone Crowdsourcing experiment users need to visit the [Smartphone Experimentation portal](https://set.organicity.eu) and select his experiment from the given list.

## Retrieving results from an experiment

Data aquired from the experiment are always available for download from the [Smartphone Experimentation portal](https://set.organicity.eu) via the appropriate links provided in the Experiments's status view. Currenlty experiments can be downloaded as JSON and CSV dumps. More options can be made avaialable in the future based on requirements.
