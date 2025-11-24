# Contribuir al proyecto Mil Sabores

Gracias por tu interés en contribuir. Esta guía describe el flujo recomendado para enviar cambios.

## Flujo de trabajo

1. Fork del repositorio.
2. Crear una rama basada en `main`:

```
git checkout -b feature/descripcion-corta
```

3. Implementar cambios y añadir tests.
4. Ejecutar `mvnw.cmd test` y verificar que todo pase.
5. Push a tu fork y abrir un Pull Request hacia `main` con una descripción clara y referencias a issues si existen.

## Convenciones de commits

- Usa mensajes en inglés o español claro. Prefijo recomendado:
  - feat: nueva funcionalidad
  - fix: corrección de bug
  - docs: cambios en documentación
  - test: añadir/actualizar tests
  - chore: tareas menores

Ejemplo:

```
feat(product): agregar endpoint de búsqueda por nombre
```

## Checklist para PR

- [ ] El código compila localmente
- [ ] Tests unitarios y de integración pasan
- [ ] Documentación actualizada si aplica
- [ ] No hay secretos en el commit

## Contacto

Dejar un comentario en el PR o contactar al mantenedor indicado en `README.md`.


