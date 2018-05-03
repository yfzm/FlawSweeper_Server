package utils;

import persistence.ItemEntity;
import persistence.TagEntity;
import persistence.UserEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InfoAPI {

    @SuppressWarnings("unchecked")
    public static List<TagEntity> getAllTags() {
        String hql = "FROM TagEntity ";
        return HibernateUtil.getSessionFactory().getCurrentSession().createQuery(hql).list();
    }

    @SuppressWarnings("unchecked")
    public static ItemEntity getItemViaPK(String item_id) {
        return HibernateUtil.getSessionFactory().getCurrentSession().get(ItemEntity.class, item_id);
    }

    public static UserEntity getUserViaPK(String user_id) {
        return HibernateUtil.getSessionFactory().getCurrentSession().get(UserEntity.class, user_id);
    }

    public static Set<TagEntity> getTagEntities(List<String> tags) {
        System.out.println("> getTagEntities");
        List<TagEntity> all_tags = InfoAPI.getAllTags();

        Set<TagEntity> tag_set = new HashSet<>();
        for (String tag : tags) {
            Boolean isNew = true;
            for (TagEntity tagEntity: all_tags) {
                if (tagEntity.getTagContent().equals(tag)) {
                    System.out.println("  find tag: " + tag);
                    tag_set.add(tagEntity);
                    isNew = false;
                    break;
                }
            }

            if (isNew) {
                System.out.println("  add new tag: " + tag);
                TagEntity tagEntity = new TagEntity();
                // TODO: use auto increment in the future
                tagEntity.setTagId(all_tags.size() + 1);
                tagEntity.setTagContent(tag);
                HibernateUtil.getSessionFactory().getCurrentSession().persist(tagEntity);
                tag_set.add(tagEntity);
            }
        }
        return tag_set;
    }
}
