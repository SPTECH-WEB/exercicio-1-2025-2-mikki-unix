package school.sptech.prova_ac1;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioRepository repository;

    public UsuarioController(UsuarioRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> buscarTodos() {
        if (repository.count() == 0) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(repository.findAll());
    }

    @PostMapping
    public ResponseEntity<Usuario> criar(@RequestBody Usuario usuario) {
        if(repository.findByEmail(usuario.getEmail()) != null
        || repository.findByCpf(usuario.getCpf()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(usuario));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Integer id) {
        Usuario usuario = repository.findById(id).stream().findFirst().orElse(null);

        if(usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok().body(usuario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        Usuario usuario = repository.findById(id).stream().findFirst().orElse(null);

        if(usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        repository.delete(usuario);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filtro-data")
    public ResponseEntity<List<Usuario>> buscarPorDataNascimento(@RequestParam LocalDate nascimento) {
        if(repository.count() == 0) {
            return ResponseEntity.noContent().build();
        }

        List<Usuario> usuarios = repository.findAllByDataNascimentoAfter(nascimento);

        if (usuarios.stream().count() == 0) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(usuarios);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable Integer id, @RequestBody Usuario usuario
    ) {
        Usuario usuarioAtualizado = repository.findById(id).stream().findFirst().orElse(null);

        if (usuarioAtualizado == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        for (Usuario u : repository.findAll()) {
            if (!u.getId().equals(id)) {
                if (u.getEmail().equals(usuario.getEmail())
                || u.getCpf().equals(usuario.getCpf())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
            }
        }

        usuarioAtualizado.setNome(usuario.getNome());
        usuarioAtualizado.setEmail(usuario.getEmail());
        usuarioAtualizado.setCpf(usuario.getCpf());
        usuarioAtualizado.setSenha(usuario.getSenha());
        usuarioAtualizado.setDataNascimento(usuario.getDataNascimento());

        return ResponseEntity.ok().body(repository.save(usuarioAtualizado));
    }
}
