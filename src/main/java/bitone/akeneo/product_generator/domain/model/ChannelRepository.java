package bitone.akeneo.product_generator.domain.model;

public interface ChannelRepository {

    public Channel get(String code);
    public int count();
    public Channel[] all();
}
