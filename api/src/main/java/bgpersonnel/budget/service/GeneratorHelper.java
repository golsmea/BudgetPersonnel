package bgpersonnel.budget.service;


import bgpersonnel.budget.exeception.GenerationRapportException;

import java.lang.reflect.Method;

public class GeneratorHelper {
    private GeneratorHelper() {
    }

    /**
     * Cette méthode permet de récupérer la valeur d'un champ d'un objet
     *
     * @param obj       l'objet
     * @param fieldName le nom du champ
     * @return la valeur du champ
     */
    public static Object getFieldValueFromObject(Object obj, String fieldName) {
        try {
            Class<?> clazz = obj.getClass();
            String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method getterMethod = clazz.getMethod(getterName);
            return getterMethod.invoke(obj);
        } catch (Exception e) {
            throw new GenerationRapportException("Error getting field value for field " + fieldName, e);
        }
    }
}
