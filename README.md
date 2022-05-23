# PROMISE: Coupling Predictive Process Mining to Process Discovery

**The repository contains code referred to the work:**

*Vincenzo Pasquadibisceglie, Annalisa Appice, Giovanna Castellano, Wil Van der Aalst*

[*PROMISE: Coupling Predictive Process Mining to Process Discovery*](https://www.sciencedirect.com/science/article/pii/S0020025522004844?via%3Dihub)

Please cite our work if you find it useful for your research and work.

```
@article{PASQUADIBISCEGLIE2022,
title = {PROMISE: Coupling Predictive Process Mining to Process Discovery},
journal = {Information Sciences},
year = {2022},
issn = {0020-0255},
doi = {https://doi.org/10.1016/j.ins.2022.05.052},
url = {https://www.sciencedirect.com/science/article/pii/S0020025522004844},
author = {Vincenzo Pasquadibisceglie and Annalisa Appice and Giovanna Castellano and Wil Van der Aalst},
keywords = {Process discovery, Predictive process mining, Deep learning, Abstraction, Event log summarization}
}
```

# How to use:
Train neural network:
```
python main.py -opt smac -event_log bpic2018insp
```
Process discovery step:

Go to the process discovery folder, e.g. Split Miner.
```
python PROMISE_SM.py -event_log bpic2018insp
```
