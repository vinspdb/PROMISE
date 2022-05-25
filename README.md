# PROMISE: Coupling Predictive Process Mining to Process Discovery

**The repository contains code referred to the work:**

*Vincenzo Pasquadibisceglie, Annalisa Appice, Giovanna Castellano, Wil Van der Aalst*

[*PROMISE: Coupling Predictive Process Mining to Process Discovery*](https://www.sciencedirect.com/science/article/pii/S0020025522004844?via%3Dihub)

Please cite our work if you find it useful for your research and work.

```
@article{PASQUADIBISCEGLIE2022250,
title = {PROMISE: Coupling predictive process mining to process discovery},
journal = {Information Sciences},
volume = {606},
pages = {250-271},
year = {2022},
issn = {0020-0255},
doi = {https://doi.org/10.1016/j.ins.2022.05.052},
url = {https://www.sciencedirect.com/science/article/pii/S0020025522004844},
author = {Vincenzo Pasquadibisceglie and Annalisa Appice and Giovanna Castellano and Wil {van der Aalst}}
}
```

# How to use:
Train neural network:
```
python main.py -opt smac -event_log <event_log>
```
Process discovery step:

Go to the process discovery folder, e.g. Split Miner.
```
python PROMISE_SM.py -event_log <event_log>
```
