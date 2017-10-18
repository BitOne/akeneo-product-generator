package bitone.akeneo.product_generator.domain.model.family;

import bitone.akeneo.product_generator.domain.model.Attribute;
import bitone.akeneo.product_generator.domain.model.Channel;

public class AttributeRequirement {

    private Attribute attribute;
    private Channel channel;

    public void Family(Attribute attribute, Channel channel) {
        this.attribute = attribute;
        this.channel = channel;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public Channel getChannel() {
        return channel;
    }
}
