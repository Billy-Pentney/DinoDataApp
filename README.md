## DinoData

A native Android Application for viewing information about prehistoric creatures (dinosaurs and related).
Data was scraped from the [Natural-History-Museum](nhm.ac.uk) website and [Wikipedia](wikipedia.org), using Python and hosted via Firebase Cloud Storage.

<img src="https://github.com/user-attachments/assets/65a7e1b9-3c23-402f-9ddc-92762893ec1d" width="200"></img>

User interface was developed using Jetpack Compose.

## Data
Creatures are described at the genus-level (e.g. "Tyrannosaurus", rather than "Tyrannosaurus rex"), and each entry contains the following fields:

* **Etymology** - includes meaning (translation); pronunciation (written and audible via Text-to-Speech audio)
* **Diet** - carnivore/piscivore/omnivore/herbivore/unknown
* **Estimated body measurements** - length, weight
* **Time Period** - Age, Epoch and Year-range, e.g. "75-65 million years ago"
* **Locations** - countries and formations in which fossils were found
* **Taxonomic lineage** - list of orders, clades, families to which the genus belongs
* **Species** - list of all known species, including discoverer and discovery year

## User-Preferences
The user has the option to mark genera as a 'Favourite' (in which case, that genus will be highlighted) and also to choose a preferred colour for each genus.

## Screenshots

| Image | Description |
| ----- | ----------- |
| <img alt="list_of_genera_by_letter" src="https://github.com/user-attachments/assets/bf84e3ed-687a-4963-abe2-5b5c7142e8e3" width="300"></img> | Homepage; a list of genera, grouped alphabetically by first letter |
| <img alt="search_with_filters" src="https://github.com/user-attachments/assets/7103df61-0c42-4108-a569-be5403d21bb8" width="300"></img> | Searching for genera with active Diet/Time-Period filters |
| <img alt="genus_detail_1" src="https://github.com/user-attachments/assets/f578c5dd-1122-48a9-8930-931d1c97bb72" width="300"></img> | Detailed genus data (Triceratops), part one |
| <img alt="genus_detail_2" src="https://github.com/user-attachments/assets/c9bbb5b6-d25a-4502-88a8-8220e555ed67" width="300"></img> | Detailed genus data (Triceratops), part two |
| <img alt="genus_detail_controls" src="https://github.com/user-attachments/assets/d2a90146-a9f1-46fb-a6bd-b086eb1247dc" width="300"></img> | Detailed genus data (Tyrannosaurus), with user controls shown |
| <img alt="genus_color_selection" src="https://github.com/user-attachments/assets/ba9d389d-3381-4709-9aa8-becdddceae3d" width="300"></img> | Selecting a preferred colour for a genus |
| <img alt="taxonomy_tree_part_open" src="https://github.com/user-attachments/assets/e62d402a-e4fa-480e-a635-8c86bc38984e" width="300"></img> | Taxonomy/phylogeny tree, where nodes can be expanded to view their children taxa |

## Attributions

* Genus silhouettes and creature images sourced from [Phylopic.org](phylopic.org) and used under CC (Creative Commons) licences as appropriate.
* Pronunciation audios generated using TTSfree.com, with Google's Libby voice
