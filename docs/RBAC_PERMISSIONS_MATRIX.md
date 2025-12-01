# Matriz de Permissões RBAC - Sistema de Chat Interno Athena

## Visão Geral

Este documento define a matriz completa de permissões do sistema de controle de acesso baseado em roles (RBAC) implementado no sistema de chat interno da Athena.

## Hierarquia de Roles

```
ADMIN (Acesso Total)
  ↓
LIDER_DE_SETOR (Permissões Amplas)
  ↓
FUNCIONARIO (Acesso Operacional)
  ↓
ESTAGIARIO (Acesso Restrito)
```

---

## Matriz de Permissões por Role

### Permissões de Usuário

| Permissão | ADMIN | LIDER_DE_SETOR | FUNCIONARIO | ESTAGIARIO |
|-----------|-------|----------------|-------------|------------|
| `USER_CREATE` | ✅ | ✅ | ❌ | ❌ |
| `USER_READ` | ✅ | ✅ | ✅ | ✅ |
| `USER_UPDATE` | ✅ | ✅ | ❌ | ❌ |
| `USER_DELETE` | ✅ | ❌ | ❌ | ❌ |

### Permissões de Grupo

| Permissão | ADMIN | LIDER_DE_SETOR | FUNCIONARIO | ESTAGIARIO |
|-----------|-------|----------------|-------------|------------|
| `GROUP_CREATE` | ✅ | ✅ | ✅ | ❌ |
| `GROUP_READ` | ✅ | ✅ | ✅ | ✅ |
| `GROUP_UPDATE` | ✅ | ✅ | ❌ | ❌ |
| `GROUP_DELETE` | ✅ | ✅ | ❌ | ❌ |
| `GROUP_MANAGE_MEMBERS` | ✅ | ✅ | ❌ | ❌ |

### Permissões de Mensagem

| Permissão | ADMIN | LIDER_DE_SETOR | FUNCIONARIO | ESTAGIARIO |
|-----------|-------|----------------|-------------|------------|
| `MESSAGE_READ` | ✅ | ✅ | ✅ | ✅ |
| `MESSAGE_SEND` | ✅ | ✅ | ✅ | ✅ |
| `MESSAGE_DELETE` | ✅ | ✅ | ❌ | ❌ |

### Permissões de Gerenciamento

| Permissão | ADMIN | LIDER_DE_SETOR | FUNCIONARIO | ESTAGIARIO |
|-----------|-------|----------------|-------------|------------|
| `ROLE_MANAGE` | ✅ | ❌ | ❌ | ❌ |

---

## Endpoints e Controle de Acesso

### Autenticação (`/auth`)

| Endpoint | Método | Roles Permitidas | Descrição |
|----------|--------|------------------|-----------|
| `/auth/login` | POST | Público | Login de usuários |
| `/auth/register` | POST | ADMIN, LIDER_DE_SETOR | Registro de novos usuários |
| `/auth/user` | GET | Autenticado | Obter dados do usuário logado |
| `/auth/singout` | POST | Autenticado | Logout |
| `/auth/{id}` | PUT | ADMIN ou próprio usuário | Atualizar dados de usuário |
| `/auth/delete/{id}` | DELETE | ADMIN | Deletar usuário |

### Grupos (`/api/grupos`)

| Endpoint | Método | Roles Permitidas | Regras Especiais |
|----------|--------|------------------|------------------|
| `/api/grupos` | GET | Autenticado | Lista todos os grupos |
| `/api/grupos/meus-grupos` | GET | Autenticado | Lista grupos criados pelo usuário |
| `/api/grupos/{id}` | GET | Autenticado | Busca grupo por ID |
| `/api/grupos` | POST | ADMIN, LIDER_DE_SETOR, FUNCIONARIO | Criar novo grupo |
| `/api/grupos/{id}` | PUT | Autenticado | **Apenas criador ou ADMIN** |
| `/api/grupos/{id}/usuarios/{userId}` | POST | Autenticado | Adicionar membro ao grupo |
| `/api/grupos/{id}/usuarios/{userId}` | DELETE | Autenticado | **Apenas criador ou ADMIN** |
| `/api/grupos/{id}/usuarios-disponiveis` | GET | Autenticado | Lista usuários não membros |

---

## Regras de Negócio Especiais

### 1. Gerenciamento de Grupos

**Regra**: Apenas o **criador do grupo** ou **ADMIN** podem:
- Atualizar informações do grupo
- Remover membros do grupo
- Deletar o grupo

**Implementação**: Validado via `PermissionService.canManageGroup()`

### 2. Criação de Grupos

**Regra**: Apenas **ADMIN**, **LIDER_DE_SETOR** e **FUNCIONARIO** podem criar grupos.

**Bloqueio**: **ESTAGIARIO** não pode criar grupos.

### 3. Registro de Usuários

**Regra**: Apenas **ADMIN** e **LIDER_DE_SETOR** podem registrar novos usuários no sistema.

**Bloqueio**: **FUNCIONARIO** e **ESTAGIARIO** não podem criar contas.

### 4. Deleção de Usuários

**Regra**: Apenas **ADMIN** pode deletar usuários do sistema.

**Restrição**: Nem mesmo **LIDER_DE_SETOR** pode deletar usuários.

### 5. Atualização de Dados de Usuário

**Regra**: Um usuário pode atualizar seus próprios dados, ou um **ADMIN** pode atualizar dados de qualquer usuário.

**Implementação**: `@PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")`

### 6. Proteção do Criador do Grupo

**Regra**: O criador de um grupo **não pode ser removido** do grupo.

**Implementação**: Validação no método `removerUsuarioDoGrupo()`

---

## Como Usar no Código

### 1. Verificar Permissão Específica

```java
@Autowired
private PermissionService permissionService;

public void exemploMetodo(User user) {
    if (permissionService.hasPermission(user, Permission.USER_CREATE)) {
        // Usuário pode criar usuários
    }
}
```

### 2. Verificar Múltiplas Permissões

```java
if (permissionService.hasAnyPermission(user, 
        Permission.GROUP_UPDATE, 
        Permission.GROUP_DELETE)) {
    // Usuário tem pelo menos uma das permissões
}
```

### 3. Verificar Gerenciamento de Grupo

```java
if (permissionService.canManageGroup(user, groupId)) {
    // Usuário é criador ou ADMIN
}
```

### 4. Usar @PreAuthorize em Controllers

```java
@PreAuthorize("hasAnyRole('ADMIN', 'LIDER_DE_SETOR')")
@PostMapping("/auth/register")
public ResponseEntity<User> register(@RequestBody UserCreateDTO data) {
    // Apenas ADMIN e LIDER_DE_SETOR podem acessar
}
```

---

## Exemplos de Casos de Uso

### Caso 1: Funcionário tenta registrar novo usuário
- **Resultado**: ❌ Acesso negado (403 Forbidden)
- **Motivo**: Apenas ADMIN e LIDER_DE_SETOR podem registrar

### Caso 2: Líder de Setor tenta deletar usuário
- **Resultado**: ❌ Acesso negado (403 Forbidden)
- **Motivo**: Apenas ADMIN pode deletar usuários

### Caso 3: Funcionário cria grupo e outro funcionário tenta remover membro
- **Resultado**: ❌ Acesso negado (AccessDeniedException)
- **Motivo**: Apenas criador ou ADMIN podem remover membros

### Caso 4: Estagiário tenta criar grupo
- **Resultado**: ❌ Acesso negado (403 Forbidden)
- **Motivo**: ESTAGIARIO não tem permissão GROUP_CREATE

### Caso 5: ADMIN remove membro de qualquer grupo
- **Resultado**: ✅ Sucesso
- **Motivo**: ADMIN sempre pode gerenciar grupos

---

## Notas de Implementação

1. **Spring Security**: Utiliza `@EnableMethodSecurity(prePostEnabled = true)` para habilitar `@PreAuthorize`

2. **Authorities**: Cada usuário recebe:
   - Role principal: `ROLE_ADMIN`, `ROLE_LIDER_DE_SETOR`, etc.
   - Permissões: `PERMISSION_USER_CREATE`, `PERMISSION_GROUP_READ`, etc.

3. **Validação em Camadas**:
   - **Controller**: `@PreAuthorize` para controle de acesso HTTP
   - **Service**: `PermissionService` para lógica de negócio complexa

4. **Exceções**:
   - `AccessDeniedException`: Lançada quando permissão negada
   - `IllegalArgumentException`: Lançada para validações de negócio

---

## Migração de Dados

Se você já possui usuários no banco de dados, será necessário atualizar as roles existentes para incluir a nova role `LIDER_DE_SETOR`.

**Script SQL sugerido**:

```sql
-- Verificar roles atuais
SELECT id, nome, email, role FROM usuarios;

-- Atualizar usuários específicos para LIDER_DE_SETOR (ajuste os IDs conforme necessário)
UPDATE usuarios SET role = 'LIDER_DE_SETOR' WHERE id IN (2, 3, 5);
```

---

## Manutenção e Extensão

Para adicionar novas permissões:

1. Adicione a permissão no enum `Permission`
2. Atualize o método `getPermissions()` em `UserRole` para incluir a nova permissão nas roles apropriadas
3. Use `@PreAuthorize` ou `PermissionService` para validar a permissão
4. Atualize esta documentação

---

**Última atualização**: 2025-12-01  
**Versão**: 1.0
