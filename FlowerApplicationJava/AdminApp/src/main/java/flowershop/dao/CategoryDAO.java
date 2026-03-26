package flowershop.dao;

import flowershop.models.Category;
import flowershop.utils.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class CategoryDAO extends BaseDAO<Category> {

    public CategoryDAO() {
        super(Category.class);
    }
    public List<Category> findRandom(int limit) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Category ORDER BY rand()",
                            Category.class
                    ).setMaxResults(limit)
                    .list();
        }
    }
}