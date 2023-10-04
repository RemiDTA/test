package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Team;
import com.example.demo.repository.TeamRepository;

import jakarta.persistence.EntityManager;

@Service
public class TeamService {

	@Autowired
	TeamRepository tr;

	@Autowired
	private EntityManager entityManager;

	public Team recupererEquipeParId(final long id) {
		final Optional<Team> optionalEquipe = this.tr.findById(id);
		if (optionalEquipe.isEmpty()) {
			throw new IllegalArgumentException(String.format("L'id %d n'existe pas", id));
		}
		return optionalEquipe.get();
	}

	public List<Team> recupererEquipeParEmplacement(final String emplacement) {
		return this.tr.findByEmplacement(emplacement);
	}

	/**
	 * Le retour de tr.save(equipe) n'est que la serialisation de equipe, or equipe est le body envoyé par l'appelant,
	 * si celui-ci a un json incomplet, je veux que l'on retourne l'objet Team au complet (l'id quant à lui est remplit au moment du save)
	 *
	 * @param equipe
	 * @return
	 */
	public Team creerEquipe(final Team equipe) {
		this.tr.save(equipe);

		// Globalement à éviter mais je le conserve au cas où
		// Le problème c'est que lorsque l'on execute le save() suivi d'un findById(), un cache est présent
		// ce cache retourne la même instance que la variable equipe, il ne rerécupère pas les données au niveau de la base
		// le this.entityManager.detach(equipe) permet justement de repartir dans la base mais normalement il n'est pas nécessaire de gérer manuellement ce genre de chose
		this.entityManager.detach(equipe);
		final Optional<Team> retour = this.tr.findById(equipe.getId());

		return retour.orElse(null);
	}

	public Team majEquipe(final Team equipe) {
		return this.tr.save(equipe);
	}

	public List<Team> recupererToutesEquipes() {
		return this.tr.findAll();
	}

	public void supprimerToutesEquipes() {
		this.tr.deleteAll();
	}

	/**
	 * On pourrait se contenter d'utiliser le deleteById, cependant si l'équipe n'existe pas, le plantage serait silencieux
	 *
	 * @param id
	 */
	public void supprimerEquipe(final long id) {
		final Team equipe = this.recupererEquipeParId(id);
		this.tr.delete(equipe);
	}

}
