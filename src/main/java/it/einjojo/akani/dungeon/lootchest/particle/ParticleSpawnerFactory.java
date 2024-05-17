package it.einjojo.akani.dungeon.lootchest.particle;

public class ParticleSpawnerFactory {

    public ParticleSpawner createParticleSpawner(String name) {
        return switch (name) {
            case NormalParticleSpawner.NAME -> new NormalParticleSpawner();
            case EpicParticleSpawner.NAME -> new EpicParticleSpawner();
            case LegendaryParticleSpawner.NAME -> new LegendaryParticleSpawner();
            default -> throw new IllegalArgumentException("Unknown particle spawner: " + name);
        };
    }

}
