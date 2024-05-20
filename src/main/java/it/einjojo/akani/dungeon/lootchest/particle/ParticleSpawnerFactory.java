package it.einjojo.akani.dungeon.lootchest.particle;

public class ParticleSpawnerFactory {

    public ParticleSpawner createParticleSpawner(String name) {
        return switch (name) {
            case NormalParticleSpawner.NAME -> createNormalParticleSpawner();
            case EpicParticleSpawner.NAME -> createEpicParticleSpawner();
            case LegendaryParticleSpawner.NAME -> createLegendaryParticleSpawner();
            default -> throw new IllegalArgumentException("Unknown particle spawner: " + name);
        };
    }

    public ParticleSpawner createNormalParticleSpawner() {
        return new NormalParticleSpawner();
    }

    public ParticleSpawner createEpicParticleSpawner() {
        return new EpicParticleSpawner();
    }

    public ParticleSpawner createLegendaryParticleSpawner() {
        return new LegendaryParticleSpawner();
    }

}
